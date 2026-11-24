package mx.fer.app

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import mx.fer.app.model.Post
import mx.fer.app.model.PostType
import mx.fer.app.model.PostsSource
import mx.fer.app.viewmodels.PostViewModel
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.util.Date

/**
 * Feed, channel filter, pull-to-refresh and post detail (RF-04 to RF-08), with a fake data
 * source instead of Firestore. Cases CP-20 to CP-27.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class PostViewModelTest {

    private class FakePosts(var posts: List<Post>) : PostsSource {
        var fail = false
        val requestedTypes = mutableListOf<PostType?>()
        val fetchedIds = mutableListOf<String>()
        val viewedIds = mutableListOf<String>()

        override suspend fun getPosts(type: PostType?): Result<List<Post>> {
            requestedTypes += type
            if (fail) return Result.failure(RuntimeException("Sin conexión"))
            return Result.success(posts.filter { type == null || it.type == type })
        }

        override suspend fun getPost(id: String): Result<Post> {
            fetchedIds += id
            if (fail) return Result.failure(RuntimeException("Sin conexión"))
            return posts.firstOrNull { it.id == id }?.let { Result.success(it) }
                ?: Result.failure(NoSuchElementException("Post $id not found"))
        }

        override suspend fun registerView(postId: String): Result<Unit> {
            viewedIds += postId
            return Result.success(Unit)
        }
    }

    private fun post(id: String, type: PostType) =
        Post(id, "Título $id", "Texto", null, type, Date())

    private val general = post("g1", PostType.GENERAL)
    private val scholars = post("s1", PostType.SCHOLARSHIP)
    private val applicants = post("a1", PostType.APPLICANTS)

    private lateinit var source: FakePosts

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        source = FakePosts(listOf(general, scholars, applicants))
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun cp20_loadsAllPostsOnStart() {
        val state = PostViewModel(source).uiState.value

        assertFalse(state.isLoading)
        assertNull(state.error)
        assertEquals(listOf("g1", "s1", "a1"), state.posts.map { it.id })
    }

    @Test
    fun cp21_filterShowsOnlyThatChannel() {
        val vm = PostViewModel(source)

        vm.onTypeSelected(PostType.SCHOLARSHIP)

        assertEquals(PostType.SCHOLARSHIP, vm.uiState.value.selectedType)
        assertEquals(listOf("s1"), vm.uiState.value.posts.map { it.id })
    }

    @Test
    fun cp22_failedLoadShowsAnError() {
        source.fail = true
        val state = PostViewModel(source).uiState.value

        assertFalse(state.isLoading)
        assertEquals("Sin conexión", state.error)
    }

    @Test
    fun cp23_refreshKeepsTheChannelAndUpdatesTheList() {
        val vm = PostViewModel(source)
        vm.onTypeSelected(PostType.APPLICANTS)
        val newer = post("a2", PostType.APPLICANTS)
        source.posts = listOf(newer) + source.posts

        vm.refresh()

        assertEquals(PostType.APPLICANTS, source.requestedTypes.last())
        assertEquals(listOf("a2", "a1"), vm.uiState.value.posts.map { it.id })
        assertFalse(vm.uiState.value.isRefreshing)
    }

    @Test
    fun cp24_failedRefreshKeepsTheCurrentList() {
        val vm = PostViewModel(source)
        source.fail = true

        vm.refresh()

        assertEquals(3, vm.uiState.value.posts.size)
        assertFalse(vm.uiState.value.isRefreshing)
    }

    @Test
    fun cp25_openingAPostFromTheListDoesNotHitTheNetwork() {
        val vm = PostViewModel(source)

        vm.selectPost("s1")

        assertEquals(scholars, vm.uiState.value.selectedPost)
        assertEquals(emptyList<String>(), source.fetchedIds)
    }

    @Test
    fun cp26_openingAPostFromAPushFetchesIt() {
        val vm = PostViewModel(source)
        vm.onTypeSelected(PostType.GENERAL)   // a1 is not in the loaded list

        vm.selectPost("a1")

        assertEquals(listOf("a1"), source.fetchedIds)
        assertEquals(applicants, vm.uiState.value.selectedPost)
    }

    @Test
    fun cp27_deletedPostFromAPushShowsAnErrorAndViewsAreCounted() {
        val vm = PostViewModel(source)

        vm.selectPost("missing")
        assertNull(vm.uiState.value.selectedPost)
        assertEquals("Post missing not found", vm.uiState.value.error)

        vm.registerView("g1")
        assertEquals(listOf("g1"), source.viewedIds)

        vm.selectPost("g1")
        vm.clearSelectedPost()
        assertNull(vm.uiState.value.selectedPost)
    }
}

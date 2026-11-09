package mx.fer.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mx.fer.app.model.AppRepository
import mx.fer.app.model.Post
import mx.fer.app.model.PostType


data class PostsUiState(
    val isLoading: Boolean = false,
    val posts: List<Post> = emptyList(),
    val selectedType: PostType? = null,   // null = show all
    val selectedPost: Post? = null,       // post shown in the detail screen
    val error: String? = null
)

class PostViewModel(
    private val repository: AppRepository = AppRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(PostsUiState())
    val uiState: StateFlow<PostsUiState> = _uiState.asStateFlow()

    init { loadPosts() }

    fun loadPosts(type: PostType? = _uiState.value.selectedType) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, selectedType = type) }

            repository.getPosts(type)
                .onSuccess { posts ->
                    _uiState.update { it.copy(isLoading = false, posts = posts) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun onTypeSelected(type: PostType?) = loadPosts(type)

    // Detail screen
    fun selectPost(id: String) {
        // Fast path: already in the loaded list
        val cached = _uiState.value.posts.firstOrNull { it.id == id }
        if (cached != null) {
            _uiState.update { it.copy(selectedPost = cached, error = null) }
            return
        }

        // Slow path: fetch it (e.g. opened from a notification)
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            repository.getPost(id)
                .onSuccess { post ->
                    _uiState.update { it.copy(isLoading = false, selectedPost = post) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun clearSelectedPost() {
        _uiState.update { it.copy(selectedPost = null) }
    }
}
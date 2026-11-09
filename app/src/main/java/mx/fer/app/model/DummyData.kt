package mx.fer.app.model

import java.util.Date
import java.util.concurrent.TimeUnit

object DummyData {

    private fun daysAgo(days: Long, hours: Long = 0): Date =
        Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(days) - TimeUnit.HOURS.toMillis(hours))

    val posts: List<Post> = listOf(
        Post(
            id = "1",
            title = "Welcome to the new semester",
            description = "Classes start next Monday. Check your schedule in the student portal and make sure your enrollment is complete before Friday.",
            linkImg = "https://picsum.photos/seed/semester/600/400",
            type = PostType.GENERAL,
            publishedAt = daysAgo(0, 2)
        ),
        Post(
            id = "2",
            title = "Excellence Scholarship 2026 is now open",
            description = "Full tuition coverage for students with a GPA of 9.0 or higher. Applications are open until the end of the month.",
            linkImg = "https://picsum.photos/seed/excellence/600/400",
            type = PostType.SCHOLARSHIP,
            publishedAt = daysAgo(1)
        ),
        Post(
            id = "3",
            title = "Applicants: admission exam dates",
            description = "The admission exam will take place on three dates. Register online and bring a valid ID on the day of the exam.",
            linkImg = null,
            type = PostType.APPLICANTS,
            publishedAt = daysAgo(2)
        ),
        Post(
            id = "4",
            title = "Library schedule changes",
            description = "Starting this week, the library will close at 8:00 PM on weekdays and will open on Saturdays from 9:00 AM to 2:00 PM.",
            linkImg = null,
            type = PostType.GENERAL,
            publishedAt = daysAgo(3)
        ),
        Post(
            id = "5",
            title = "Sports Scholarship: tryouts announced",
            description = "Students interested in the sports scholarship must attend the tryouts at the main field. Bring sportswear and your medical certificate.",
            linkImg = "https://picsum.photos/seed/sports/600/400",
            type = PostType.SCHOLARSHIP,
            publishedAt = daysAgo(4)
        ),
        Post(
            id = "6",
            title = "Open house for future students",
            description = "Come and meet our campus, professors and programs. Tours start every hour, and there will be talks about admission requirements.",
            linkImg = "https://picsum.photos/seed/openhouse/600/400",
            type = PostType.APPLICANTS,
            publishedAt = daysAgo(6)
        ),
        Post(
            id = "7",
            title = "Maintenance on the student portal",
            description = "The portal will be unavailable on Sunday from 1:00 AM to 5:00 AM due to scheduled maintenance.",
            linkImg = null,
            type = PostType.GENERAL,
            publishedAt = daysAgo(8)
        ),
        Post(
            id = "8",
            title = "Scholarship results published",
            description = "The results of the previous call are now available. Selected students must confirm their place within five business days.",
            linkImg = "https://picsum.photos/seed/results/600/400",
            type = PostType.SCHOLARSHIP,
            publishedAt = daysAgo(10)
        ),
        Post(
            id = "9",
            title = "Applicants: required documents checklist",
            description = "Make sure you have your birth certificate, high school transcript and three photos ready before starting your application.",
            linkImg = null,
            type = PostType.APPLICANTS,
            publishedAt = daysAgo(12)
        ),
        Post(
            id = "10",
            title = "Cultural week schedule",
            description = "Music, theater and art exhibitions all week long. Entry is free for students with a valid ID.",
            linkImg = "https://picsum.photos/seed/culture/600/400",
            type = PostType.GENERAL,
            publishedAt = daysAgo(15)
        )
    )

    val notifications: List<Notification> = listOf(
        Notification(
            id = "n1",
            idPublication = "2",
            title = "New scholarship available",
            message = "The Excellence Scholarship 2026 is now open.",
            type = PostType.SCHOLARSHIP,
            date = daysAgo(1)
        ),
        Notification(
            id = "n2",
            idPublication = "3",
            title = "Admission exam dates",
            message = "Check the dates for the admission exam.",
            type = PostType.APPLICANTS,
            date = daysAgo(2)
        ),
        Notification(
            id = "n3",
            idPublication = null,
            title = "Reminder",
            message = "Complete your enrollment before Friday.",
            type = PostType.GENERAL,
            date = daysAgo(3)
        )
    )
}
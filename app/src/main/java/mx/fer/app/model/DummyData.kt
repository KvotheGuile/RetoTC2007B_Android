package mx.fer.app.model

import java.util.Date
import java.util.concurrent.TimeUnit

object DummyData {

    private fun daysAgo(days: Long, hours: Long = 0): Date =
        Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(days) - TimeUnit.HOURS.toMillis(hours))

    // Imágenes de prueba (picsum.photos). Cada "seed" da una foto distinta.
    private fun img(seed: String) = "https://picsum.photos/seed/$seed/600/400"

    val posts: List<Post> = listOf(
        Post(
            id = "1",
            title = "Convocatoria de becas 2026",
            description = "Conoce las fechas, requisitos y pasos para participar en la nueva convocatoria de becas de la Fundación Esposos Rodríguez.",
            linkImg = img("convocatoria"),
            type = PostType.APPLICANTS,
            publishedAt = daysAgo(0, 2)
        ),
        Post(
            id = "2",
            title = "Renovación de becas 2026",
            description = "Ya se encuentran disponibles las fechas y requisitos para renovar tu beca. Consulta la información completa y entrega tus documentos a tiempo.",
            linkImg = img("renovacion"),
            type = PostType.SCHOLARSHIP,
            publishedAt = daysAgo(1)
        ),
        Post(
            id = "3",
            title = "Sesión informativa para aspirantes",
            description = "Te invitamos a conocer el proceso de selección, los requisitos y los beneficios de la beca. Resolveremos todas tus dudas en vivo.",
            linkImg = null,
            type = PostType.APPLICANTS,
            publishedAt = daysAgo(2)
        ),
        Post(
            id = "4",
            title = "Taller de desarrollo profesional",
            description = "Participa en los talleres y actividades que preparamos para nuestra comunidad. Aprende a elaborar tu currículum y a preparar entrevistas.",
            linkImg = img("taller"),
            type = PostType.GENERAL,
            publishedAt = daysAgo(3)
        ),
        Post(
            id = "5",
            title = "Resultados de convocatoria",
            description = "Consulta los resultados y las fechas importantes del proceso de selección. Los becarios seleccionados deberán confirmar su lugar en cinco días hábiles.",
            linkImg = img("resultados"),
            type = PostType.SCHOLARSHIP,
            publishedAt = daysAgo(4)
        ),
        Post(
            id = "6",
            title = "Documentos requeridos para aspirantes",
            description = "Ten listos tu acta de nacimiento, historial académico, comprobante de domicilio y carta de motivos antes de iniciar tu solicitud.",
            linkImg = img("documentos"),
            type = PostType.APPLICANTS,
            publishedAt = daysAgo(5)
        ),
        Post(
            id = "7",
            title = "Actividades de octubre",
            description = "Consulta las próximas actividades, eventos y novedades de la fundación. Habrá pláticas, convivencias y jornadas de voluntariado.",
            linkImg = img("octubre"),
            type = PostType.GENERAL,
            publishedAt = daysAgo(7)
        ),
        Post(
            id = "8",
            title = "Entrega de documentos de renovación",
            description = "Recuerda que la entrega de documentos para renovar tu beca cierra el viernes. No se aceptarán expedientes incompletos.",
            linkImg = null,
            type = PostType.SCHOLARSHIP,
            publishedAt = daysAgo(8)
        ),
        Post(
            id = "9",
            title = "Pláticas de orientación vocacional",
            description = "Si estás por elegir carrera, acompáñanos a las pláticas con egresados y profesionistas que compartirán su experiencia.",
            linkImg = img("orientacion"),
            type = PostType.APPLICANTS,
            publishedAt = daysAgo(9)
        ),
        Post(
            id = "10",
            title = "Reunión general de becarios",
            description = "Nos reuniremos para compartir avances del ciclo, resolver dudas y presentar las nuevas actividades de acompañamiento.",
            linkImg = img("reunion"),
            type = PostType.SCHOLARSHIP,
            publishedAt = daysAgo(11)
        ),
        Post(
            id = "11",
            title = "Jornada de voluntariado",
            description = "Súmate a nuestra jornada de voluntariado en la comunidad. Habrá actividades para todas las edades y se entregará constancia de participación.",
            linkImg = null,
            type = PostType.GENERAL,
            publishedAt = daysAgo(13)
        ),
        Post(
            id = "12",
            title = "Cambios en las fechas de entrevistas",
            description = "Por motivos de logística, las entrevistas de selección se reprogramaron. Revisa tu correo para confirmar tu nuevo horario.",
            linkImg = null,
            type = PostType.APPLICANTS,
            publishedAt = daysAgo(15)
        ),
        Post(
            id = "13",
            title = "Becarios destacados del ciclo",
            description = "Felicitamos a los becarios que obtuvieron los mejores promedios del ciclo. Gracias por su esfuerzo y compromiso.",
            linkImg = null,
            type = PostType.SCHOLARSHIP,
            publishedAt = daysAgo(18)
        ),
        Post(
            id = "14",
            title = "Aniversario de la Fundación",
            description = "Celebramos un año más de apoyar a jóvenes con talento. Gracias a nuestros donantes, becarios y aliados por ser parte de esta historia.",
            linkImg = img("aniversario"),
            type = PostType.GENERAL,
            publishedAt = daysAgo(21)
        ),
        Post(
            id = "15",
            title = "Convocatoria abierta: beca de excelencia",
            description = "Beca completa para estudiantes con promedio de 9.0 o mayor. El registro estará abierto hasta el último día del mes.",
            linkImg = img("excelencia"),
            type = PostType.APPLICANTS,
            publishedAt = daysAgo(25)
        ),
        Post(
            id = "16",
            title = "Curso de inglés para becarios",
            description = "Abrimos inscripciones al curso de inglés con clases en línea y presenciales. Los lugares son limitados, así que regístrate cuanto antes.",
            linkImg = img("ingles"),
            type = PostType.SCHOLARSHIP,
            publishedAt = daysAgo(27)
        ),
        Post(
            id = "17",
            title = "Mentorías con egresados",
            description = "Conoce a los egresados que te acompañarán durante el ciclo. Escríbenos si quieres solicitar una mentoría personalizada.",
            linkImg = null,
            type = PostType.SCHOLARSHIP,
            publishedAt = daysAgo(28)
        ),
        Post(
            id = "18",
            title = "Servicio social y prácticas profesionales",
            description = "Consulta las vacantes disponibles con nuestras empresas aliadas y los pasos para registrar tu servicio social o tus prácticas.",
            linkImg = img("practicas"),
            type = PostType.SCHOLARSHIP,
            publishedAt = daysAgo(33)
        ),
        Post(
            id = "19",
            title = "Guía para preparar tu examen de admisión",
            description = "Reunimos recomendaciones, temas clave y ejercicios de práctica para que llegues con confianza al día de tu examen.",
            linkImg = img("guia"),
            type = PostType.APPLICANTS,
            publishedAt = daysAgo(29)
        ),
        Post(
            id = "20",
            title = "Preguntas frecuentes de la convocatoria",
            description = "Resolvemos las dudas más comunes sobre requisitos, fechas, documentos y el proceso de selección. Revísalas antes de escribirnos.",
            linkImg = null,
            type = PostType.APPLICANTS,
            publishedAt = daysAgo(31)
        ),
        Post(
            id = "21",
            title = "Feria de universidades",
            description = "Visita la feria y conoce las opciones de carrera de distintas universidades. Habrá asesoría personalizada y entrega de material informativo.",
            linkImg = img("feria"),
            type = PostType.APPLICANTS,
            publishedAt = daysAgo(35)
        ),
        Post(
            id = "22",
            title = "Campaña de útiles escolares",
            description = "Ayúdanos a reunir útiles escolares para niñas y niños de la comunidad. Puedes entregar tu donación en nuestras oficinas.",
            linkImg = img("utiles"),
            type = PostType.GENERAL,
            publishedAt = daysAgo(30)
        ),
        Post(
            id = "23",
            title = "Horario de oficinas por día festivo",
            description = "Nuestras oficinas permanecerán cerradas el lunes por día festivo. Reanudaremos la atención el martes en el horario habitual.",
            linkImg = null,
            type = PostType.GENERAL,
            publishedAt = daysAgo(32)
        ),
        Post(
            id = "24",
            title = "Conferencia: finanzas personales",
            description = "Aprende a organizar tus gastos, ahorrar y planear tu futuro con especialistas invitados. La entrada es libre y habrá constancia.",
            linkImg = img("finanzas"),
            type = PostType.GENERAL,
            publishedAt = daysAgo(38)
        )
    )

    val notifications: List<Notification> = listOf(
        Notification(
            id = "n1",
            idPublication = "1",
            title = "Nueva convocatoria",
            message = "Ya está abierta la convocatoria de becas 2026.",
            type = PostType.APPLICANTS,
            date = daysAgo(0, 2)
        ),
        Notification(
            id = "n2",
            idPublication = "2",
            title = "Renovación de becas",
            message = "Consulta las fechas y requisitos para renovar tu beca.",
            type = PostType.SCHOLARSHIP,
            date = daysAgo(1)
        ),
        Notification(
            id = "n3",
            idPublication = "3",
            title = "Sesión informativa",
            message = "Conoce el proceso de selección en nuestra próxima sesión.",
            type = PostType.APPLICANTS,
            date = daysAgo(2)
        ),
        Notification(
            id = "n4",
            idPublication = "4",
            title = "Taller disponible",
            message = "Ya puedes inscribirte al taller de desarrollo profesional.",
            type = PostType.GENERAL,
            date = daysAgo(3)
        ),
        Notification(
            id = "n5",
            idPublication = "5",
            title = "Resultados publicados",
            message = "Ya puedes consultar los resultados de la convocatoria.",
            type = PostType.SCHOLARSHIP,
            date = daysAgo(4)
        ),
        Notification(
            id = "n6",
            idPublication = "12",
            title = "Cambio de fechas",
            message = "Las entrevistas de selección fueron reprogramadas.",
            type = PostType.APPLICANTS,
            date = daysAgo(15)
        ),
        Notification(
            id = "n7",
            idPublication = "16",
            title = "Curso de inglés",
            message = "Abrimos inscripciones al curso de inglés para becarios.",
            type = PostType.SCHOLARSHIP,
            date = daysAgo(27)
        ),
        Notification(
            id = "n8",
            idPublication = null,
            title = "Recordatorio",
            message = "Entrega tus documentos de renovación antes del viernes.",
            type = PostType.GENERAL,
            date = daysAgo(8)
        )
    )
}
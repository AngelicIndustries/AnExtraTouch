import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication

val clientOnlyModIds = listOf(
    "angelica",
    "shouldersurfing",
    "doabarrelroll"
)

tasks.withType<JavaExec>().configureEach {
    if (name.startsWith("runServer")) {
        doFirst("stripClientOnlyMods") {
            classpath = classpath.filter { file ->
                clientOnlyModIds.none { modId -> file.name.contains(modId, ignoreCase = true) }
            }
        }
    }
}
extensions.configure<PublishingExtension> {
    publications.withType<MavenPublication>().configureEach {
        pom {
            name.set("An Extra Touch")
            description.set("Visual, audio, and gameplay tweaks for Minecraft 1.7.10.")
            url.set("https://github.com/AngelicIndustries/AnExtraTouch")
            organization {
                name.set("Angelic Industries")
                url.set("https://angelic.industries/")
            }
            scm {
                url.set("https://github.com/AngelicIndustries/AnExtraTouch")
                connection.set("scm:git:https://github.com/AngelicIndustries/AnExtraTouch.git")
                developerConnection.set("scm:git:ssh://git@github.com/AngelicIndustries/AnExtraTouch.git")
            }
            licenses {
                license {
                    name.set("GNU Lesser General Public License, version 3")
                    url.set("https://www.gnu.org/licenses/lgpl-3.0.html")
                    distribution.set("repo")
                }
            }
        }
    }
}

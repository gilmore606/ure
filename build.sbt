name := "ure"
organization := "ure"
version := "0.1"
description := "UnRoguelike Engine"

javaOptions += "-XstartOnFirstThread"
javaOptions += "-Dorg.lwjgl.util.Debug=true"
javaOptions += "-Dorg.lwjgl.opengl.Display.enableHighDPI=true"

Compile / run / fork := true

libraryDependencies += "com.fasterxml.jackson.core" % "jackson-core" % "2.9.6"
libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % "2.9.6"
libraryDependencies += "com.fasterxml.jackson.core" % "jackson-annotations" % "2.9.6"

libraryDependencies ++= {
  val version = "3.1.6"

  Seq(
    "lwjgl",
    "lwjgl-glfw",
    "lwjgl-opengl",
    "lwjgl-stb"
  ).flatMap {
    module => {
      Seq(
        "org.lwjgl" % module % version,
        "org.lwjgl" % module % version  classifier "natives-linux" classifier "natives-macos" classifier "natives-windows"
      )
    }
  }
}

libraryDependencies += "org.reflections" % "reflections" % "0.9.11"
libraryDependencies += "org.joml" % "joml" % "1.9.9"
libraryDependencies += "commons-io" % "commons-io" % "2.6"

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}
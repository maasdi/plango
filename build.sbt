name := "plango"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaEbean,
  "org.apache.httpcomponents" % "httpclient" % "4.3.1",
  "org.apache.httpcomponents" % "httpcore" % "4.3.1",
  "leodagdag" %% "play2-morphia-plugin" % "0.0.17",
  "be.objectify" %% "deadbolt-java" % "2.2-RC5",
  "net.sourceforge.htmlunit" % "htmlunit" % "2.14" % "test"
)

resolvers += "Mike Nikles repository" at "http://mikenikles.github.io/repository/"

resolvers += Resolver.url("Objectify Play Repository", url("http://deadbolt.ws/releases/"))(Resolver.ivyStylePatterns)

play.Project.playJavaSettings

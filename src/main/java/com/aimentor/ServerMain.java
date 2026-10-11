package com.aimentor;

import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;

import java.io.File;

/**
 * Embedded Tomcat 10.1 runner.
 * Enables running the entire Adaptive AI Coding Mentor application with a single command:
 *   mvn compile exec:java
 * or by launching start.bat.
 */
public class ServerMain {
    public static void main(String[] args) throws Exception {
        int port = 8080;
        String portEnv = System.getenv("PORT");
        if (portEnv != null && !portEnv.isBlank()) {
            try {
                port = Integer.parseInt(portEnv.trim());
            } catch (NumberFormatException ignored) {}
        }

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);
        tomcat.setBaseDir(new File("target/tomcat").getAbsolutePath());
        tomcat.getConnector(); // Initialize default HTTP connector

        // Seed 280+ popular interview questions if database has fewer than 150
        try {
            com.aimentor.util.InterviewQuestionSeeder.seedIfEmpty();
        } catch (Exception e) {
            System.err.println("Could not auto-seed interview questions: " + e.getMessage());
        }

        // Webapp root directory
        File webappDir = new File("src/main/webapp");
        if (!webappDir.exists()) {
            // Check if running from parent directory
            webappDir = new File("ai-coding-mentor/src/main/webapp");
        }
        String webappDirLocation = webappDir.getAbsolutePath();

        StandardContext ctx = (StandardContext) tomcat.addWebapp("", webappDirLocation);
        ctx.setParentClassLoader(ServerMain.class.getClassLoader());

        // Map compiled classes so @WebServlet and @WebFilter annotations are discovered
        File classesDir = new File("target/classes");
        if (!classesDir.exists()) {
            classesDir = new File("ai-coding-mentor/target/classes");
        }

        if (classesDir.exists()) {
            WebResourceRoot resources = new StandardRoot(ctx);
            resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes",
                    classesDir.getAbsolutePath(), "/"));
            ctx.setResources(resources);
        }

        System.out.println("\n==================================================================");
        System.out.println("  >>> ADAPTIVE AI CODING MENTOR IS RUNNING! <<<");
        System.out.println("  Access Application at: http://localhost:" + port);
        System.out.println("  Admin Portal:          http://localhost:" + port + "/admin/dashboard");
        System.out.println("  Student Workspace:     http://localhost:" + port + "/app/");
        System.out.println("  (Press Ctrl+C to stop the server)");
        System.out.println("==================================================================\n");

        tomcat.start();
        tomcat.getServer().await();
    }
}

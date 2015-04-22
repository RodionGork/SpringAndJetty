package eventrouter;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class App {

    public static void main(String[] args) throws Exception {
        new App().run();
    }

    public void run() throws Exception {
        Server server = createHttpsServer();
        //server.setHandler(new CustomHandler());
        server.setHandler(createHandler(createSpringContext()));
        server.start();
        server.join();
    }
    
    private Server createBasicServer() {
        return new Server(8080);
    }
    
    private Server createHttpsServer() {
        Server server = new Server();
        HttpConfiguration https = new HttpConfiguration();
        https.addCustomizer(new SecureRequestCustomizer());
        SslContextFactory sslCtxFactory = new SslContextFactory();
        sslCtxFactory.setKeyStorePath(this.getClass().getResource("/keystore.jks").toExternalForm());
        sslCtxFactory.setKeyStorePassword("123456");
        sslCtxFactory.setKeyManagerPassword("123456");
        
        ServerConnector conn = new ServerConnector(server);
        conn.setPort(8080);
        ServerConnector conns = new ServerConnector(server, new SslConnectionFactory(sslCtxFactory, "http/1.1"), new HttpConnectionFactory(https));
        conns.setPort(8083);
        server.setConnectors(new ServerConnector[] {conn, conns});
        return server;
    }

    public static class CustomHandler extends AbstractHandler {

        @Override
        public void handle(String s, Request baseReq, HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
            resp.setContentType("text/html");
            resp.setStatus(HttpServletResponse.SC_OK);
            baseReq.setHandled(true);
            resp.getWriter().println("<h1>Zlopaka</h1>");
        }
    }

    private ServletContextHandler createHandler(WebApplicationContext context) {
        ServletContextHandler handler = new ServletContextHandler();
        handler.setErrorHandler(null);
        handler.setContextPath("/");
        handler.addServlet(new ServletHolder(new DispatcherServlet(context)), "/");
        handler.addEventListener(new ContextLoaderListener(context));
        return handler;
    }

    private WebApplicationContext createSpringContext() {
        AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
        ctx.setConfigLocation("eventrouter.config");
        return ctx;
    }

}

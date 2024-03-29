package ru.netology.servlet;

import ru.netology.controller.PostController;
import ru.netology.repository.PostRepository;
import ru.netology.service.PostService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MainServlet extends HttpServlet {
  private PostController controller;
  private final static String GET = "GET";
  private final static String POST = "POST";
  private final static String DELETE = "DELETE";
  private final static String apiPath = "/api/posts";
  private final static char slash = '/';
  private final static String longPattern = "\\d+";

  @Override
  public void init() {
    final var repository = new PostRepository();
    final var service = new PostService(repository);
    controller = new PostController(service);
  }

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp) {
    // если деплоились в root context, то достаточно этого
    try {
      final var path = req.getRequestURI();
      final var method = req.getMethod();
      // primitive routing
      if (method.equals(GET) && path.equals(apiPath)) {
        controller.all(resp);
        return;
      }
      if ((method.equals(GET) || method.equals(DELETE)) && path.matches(apiPath + slash + longPattern)) {
        // easy way
        final var id = Long.parseLong(path.substring(path.lastIndexOf(slash) + 1));
        if (method.equals(GET)){
          controller.getById(id, resp);
        } else {
          controller.removeById(id, resp);
        }
        return;
      }
      if (method.equals(POST) && path.equals(apiPath)) {
        controller.save(req.getReader(), resp);
        return;
      }
      resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    } catch (Exception e) {
      e.printStackTrace();
      resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }
}


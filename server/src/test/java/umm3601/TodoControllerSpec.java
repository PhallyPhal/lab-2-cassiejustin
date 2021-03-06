package umm3601;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import io.javalin.core.validation.Validator;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import todo.Todo;
import todo.TodoController;
import todo.TodoDatabase;
import umm3601.Server;


public class TodoControllerSpec {

  private Context ctx = mock(Context.class);

  private TodoController todoController;
  private static TodoDatabase db;

  @BeforeEach
  public void setUp() throws IOException {
    ctx.clearCookieStore();

    db = new TodoDatabase(Server.TODO_DATA_FILE);
    todoController = new TodoController(db);
  }



  @Test
  public void GET_to_request_todos_with_incomplete_status(){
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("status", Arrays.asList(new String[] { "incomplete" }));

    when(ctx.queryParamMap()).thenReturn(queryParams);
    todoController.getTodos(ctx);

    //Confirm that all the todos passed to `json` have false status
    ArgumentCaptor<Todo[]> argument = ArgumentCaptor.forClass(Todo[].class);
    verify(ctx).json(argument.capture());
    assertTrue(argument.getValue().length > 20);
    for (Todo todo : argument.getValue()) {
      assertEquals(false, todo.status);
    }
  }

  @Test
  public void GET_to_request_todos_with_contains_body(){
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("contains", Arrays.asList(new String[] { "nisi" }));

    when(ctx.queryParamMap()).thenReturn(queryParams);
    todoController.getTodos(ctx);

    //Confirm that all the todos passed to `json` have false status
    ArgumentCaptor<Todo[]> argument = ArgumentCaptor.forClass(Todo[].class);
    verify(ctx).json(argument.capture());
    assertTrue(argument.getValue().length > 5);
    for (Todo todo : argument.getValue()) {
      assertEquals(true, todo.body.contains("nisi"));
    }
  }


}

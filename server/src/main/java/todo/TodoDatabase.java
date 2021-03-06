package todo;


import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import io.javalin.http.BadRequestResponse;

public class TodoDatabase {

  private Todo[] allTodos;

  public TodoDatabase(String todoDataFile) throws IOException {

    Gson gson = new Gson();
    InputStreamReader reader = new InputStreamReader(getClass().getResourceAsStream(todoDataFile));
    allTodos = gson.fromJson(reader, Todo[].class);
  }

  public int size() {
    return allTodos.length;
  }

  /**
   * Get an array of all the users satisfying the queries in the params.
   *
   * @param queryParams map of key-value pairs for the query
   * @return an array of all the users matching the given criteria
   */
  public Todo[] listTodos(Map<String, List<String>> queryParams) {
    Todo[] filteredTodos = allTodos;

    // Filter by status if defined
    if (queryParams.containsKey("status")) {
      String statusParam = queryParams.get("status").get(0);
      boolean targetStatus;
        if (statusParam == "incomplete"){
          targetStatus = false;
        }
        else {
          targetStatus = true;
        }
      filteredTodos = filterTodosByStatus(filteredTodos, targetStatus);
    }
    // filter by limit if defined
    if (queryParams.containsKey("limit")){
      String limitParam = queryParams.get("limit").get(0);
      try{
        int targetLimit = Integer.parseInt(limitParam);
        filteredTodos = filterTodosByLimit(filteredTodos, targetLimit);
      } catch (NumberFormatException e){
        throw new BadRequestResponse("Specified status '" + limitParam + "' can't be parsed to an integer");
      }
    }
    // filter by body if defined
    if (queryParams.containsKey("contains")){
      String bodyParam = queryParams.get("contains").get(0);
      filteredTodos = filterTodosByContains(filteredTodos, bodyParam);
    }
    return filteredTodos;
  }


    /**
   * Get the single user specified by the given ID. Return `null` if there is no
   * user with that ID.
   *
   * @param id the ID of the desired user
   * @return the user with the given ID, or null if there is no user with that ID
   */
  public Todo getTodo(String id) {
    return Arrays.stream(allTodos).filter(x -> x._id == (id)).findFirst().orElse(null);
  }

  public Todo[] filterTodosByStatus(Todo[] todos, boolean status){
    return Arrays.stream(todos).filter(x -> x.status == (status)).toArray(Todo[]::new);

  }

  public Todo[] filterTodosByLimit(Todo[] todos, int limit){
    return Arrays.copyOfRange(todos, 0, limit);
  }

  public Todo[] filterTodosByContains(Todo[] todos, String string){
    return Arrays.stream(todos).filter(x-> x.body.contains(string)).toArray(Todo[]::new);
  }



}

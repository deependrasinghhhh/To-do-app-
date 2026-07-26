const apiUrl = "/api/todos";

let todos = [];
let activeFilter = "all";
let editingTodoId = null;

const form = document.querySelector("#todo-form");
const titleInput = document.querySelector("#todo-title");
const dueDateInput = document.querySelector("#todo-due-date");
const priorityInput = document.querySelector("#todo-priority");
const todoList = document.querySelector("#todo-list");
const todoCount = document.querySelector("#todo-count");
const message = document.querySelector("#message");
const filterButtons = document.querySelectorAll(".filter-button");
const toggleAllButton = document.querySelector("#toggle-all-button");
const clearCompletedButton = document.querySelector("#clear-completed-button");

async function request(url, options = {}) {
  const response = await fetch(url, {
    headers: { "Content-Type": "application/json", ...options.headers },
    ...options
  });

  if (!response.ok) {
    const error = await response.json().catch(() => ({ message: "Something went wrong." }));
    throw new Error(error.message || "Something went wrong.");
  }

  return response.status === 204 ? null : response.json();
}

async function loadTodos() {
  try {
    todos = await request(apiUrl);
    render();
  } catch (error) {
    showMessage(error.message, true);
  }
}

function filteredTodos() {
  if (activeFilter === "active") {
    return todos.filter((todo) => !todo.completed);
  }
  if (activeFilter === "completed") {
    return todos.filter((todo) => todo.completed);
  }
  return todos;
}

function updateToolbarState() {
  const hasTodos = todos.length > 0;
  const hasCompleted = todos.some((todo) => todo.completed);
  const hasIncomplete = todos.some((todo) => !todo.completed);

  toggleAllButton.disabled = !hasTodos;
  toggleAllButton.textContent = hasIncomplete ? "Complete all" : "Reset all";
  clearCompletedButton.disabled = !hasCompleted;
}

function render() {
  const visibleTodos = filteredTodos();
  const activeCount = todos.filter((todo) => !todo.completed).length;

  todoList.replaceChildren();
  todoCount.textContent = `${activeCount} ${activeCount === 1 ? "item" : "items"} left`;
  updateToolbarState();

  if (visibleTodos.length === 0) {
    const emptyState = document.createElement("li");
    emptyState.className = "empty-state";
    emptyState.textContent = todos.length === 0
      ? "Your list is clear. Add a first todo above."
      : "No todos match this filter.";
    todoList.append(emptyState);
    return;
  }

  visibleTodos.forEach((todo) => {
    todoList.append(createTodoElement(todo));
  });
}

function createActionButton(label, className, handler) {
  const button = document.createElement("button");
  button.type = "button";
  button.className = className;
  button.textContent = label;
  button.addEventListener("click", handler);
  return button;
}

function createTodoElement(todo) {
  const item = document.createElement("li");
  item.className = `todo-item${todo.completed ? " is-completed" : ""}`;

  const checkbox = document.createElement("input");
  checkbox.type = "checkbox";
  checkbox.checked = todo.completed;
  checkbox.setAttribute("aria-label", `Mark ${todo.title} as ${todo.completed ? "active" : "complete"}`);
  checkbox.addEventListener("change", () => updateTodo(todo.id, { completed: checkbox.checked }));

  const content = document.createElement("div");
  content.className = "todo-content";

  if (editingTodoId === todo.id) {
    const input = document.createElement("input");
    input.type = "text";
    input.className = "todo-edit-input";
    input.value = todo.title;
    input.setAttribute("aria-label", `Edit title for ${todo.title}`);
    input.addEventListener("keydown", async (event) => {
      if (event.key === "Enter") {
        event.preventDefault();
        await saveEditedTodo(todo, input.value);
      }
      if (event.key === "Escape") {
        event.preventDefault();
        cancelEditing();
      }
    });

    const saveButton = createActionButton("Save", "action-button save-button", async () => {
      await saveEditedTodo(todo, input.value);
    });
    const cancelButton = createActionButton("Cancel", "action-button cancel-button", () => {
      cancelEditing();
    });

    content.append(input, saveButton, cancelButton);
  } else {
    const title = document.createElement("span");
    title.className = "todo-title";
    title.textContent = todo.title;

    const meta = document.createElement("div");
    meta.className = "todo-meta";

    if (todo.dueDate) {
      const dueDate = document.createElement("span");
      dueDate.textContent = `Due ${new Date(todo.dueDate).toLocaleDateString("en-US", { month: "short", day: "numeric" })}`;
      meta.append(dueDate);
    }

    if (todo.priority) {
      const priority = document.createElement("span");
      priority.className = `priority-pill ${todo.priority.toLowerCase()}`;
      priority.textContent = todo.priority;
      meta.append(priority);
    }

    content.append(title, meta);
  }

  const actions = document.createElement("div");
  actions.className = "todo-actions";

  if (editingTodoId === todo.id) {
    actions.append(document.createTextNode(""));
  } else {
    actions.append(
      createActionButton("Edit", "action-button edit-button", () => startEditing(todo)),
      createActionButton("Delete", "action-button delete-button", () => deleteTodo(todo))
    );
  }

  item.append(checkbox, content, actions);
  return item;
}

async function updateTodo(id, changes) {
  try {
    await request(`${apiUrl}/${id}`, {
      method: "PATCH",
      body: JSON.stringify(changes)
    });
    await loadTodos();
  } catch (error) {
    showMessage(error.message, true);
    await loadTodos();
  }
}

function startEditing(todo) {
  editingTodoId = todo.id;
  render();
  window.requestAnimationFrame(() => {
    const input = todoList.querySelector(".todo-edit-input");
    input?.focus();
    input?.select();
  });
}

function cancelEditing() {
  editingTodoId = null;
  render();
}

async function saveEditedTodo(todo, rawTitle) {
  const title = rawTitle.trim();
  if (!title) {
    showMessage("Todo title cannot be empty.", true);
    return;
  }

  if (title === todo.title) {
    cancelEditing();
    return;
  }

  editingTodoId = null;
  render();
  await updateTodo(todo.id, { title });
}

async function deleteTodo(todo) {
  if (!window.confirm(`Delete “${todo.title}”?`)) {
    return;
  }

  try {
    await request(`${apiUrl}/${todo.id}`, { method: "DELETE" });
    showMessage("Todo deleted.");
    await loadTodos();
  } catch (error) {
    showMessage(error.message, true);
  }
}

async function clearCompletedTodos() {
  const completedTodos = todos.filter((todo) => todo.completed);
  if (completedTodos.length === 0) {
    return;
  }

  if (!window.confirm(`Clear ${completedTodos.length} completed todo${completedTodos.length === 1 ? "" : "s"}?`)) {
    return;
  }

  try {
    await Promise.all(completedTodos.map((todo) => request(`${apiUrl}/${todo.id}`, { method: "DELETE" })));
    showMessage("Completed todos cleared.");
    await loadTodos();
  } catch (error) {
    showMessage(error.message, true);
  }
}

async function toggleAllTodos() {
  const shouldComplete = todos.some((todo) => !todo.completed);
  const pendingTodos = todos.filter((todo) => todo.completed !== shouldComplete);

  if (pendingTodos.length === 0) {
    showMessage(shouldComplete ? "Everything is already complete." : "Everything is already active.");
    return;
  }

  try {
    await Promise.all(pendingTodos.map((todo) => request(`${apiUrl}/${todo.id}`, {
      method: "PATCH",
      body: JSON.stringify({ completed: shouldComplete })
    })));
    showMessage(shouldComplete ? "All todos completed." : "All todos marked active.");
    await loadTodos();
  } catch (error) {
    showMessage(error.message, true);
  }
}

function showMessage(text, isError = false) {
  message.textContent = text;
  message.classList.toggle("is-error", isError);

  if (!isError && text) {
    window.setTimeout(() => {
      if (message.textContent === text) {
        message.textContent = "";
      }
    }, 2500);
  }
}

form.addEventListener("submit", async (event) => {
  event.preventDefault();
  const title = titleInput.value.trim();

  if (!title) {
    showMessage("Please enter a todo title.", true);
    return;
  }

  try {
    await request(apiUrl, {
      method: "POST",
      body: JSON.stringify({
        title,
        dueDate: dueDateInput.value || null,
        priority: priorityInput.value
      })
    });
    titleInput.value = "";
    dueDateInput.value = "";
    priorityInput.value = "MEDIUM";
    titleInput.focus();
    showMessage("Todo added.");
    await loadTodos();
  } catch (error) {
    showMessage(error.message, true);
  }
});

for (const button of filterButtons) {
  button.addEventListener("click", () => {
    activeFilter = button.dataset.filter;
    filterButtons.forEach((filterButton) => {
      filterButton.classList.toggle("is-selected", filterButton === button);
    });
    render();
  });
}

toggleAllButton.addEventListener("click", () => {
  void toggleAllTodos();
});

clearCompletedButton.addEventListener("click", () => {
  void clearCompletedTodos();
});

loadTodos();

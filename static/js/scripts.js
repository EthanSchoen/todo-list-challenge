function addTask(taskName) {
    if (taskName.trim() == "") {  // do nothing if input is whitespace or empty
        return;
    }

    var table = document.getElementById("list");
    var UID = Date.now();  // lazy UID for rows
    var row = table.insertRow(-1);

    row.setAttribute("id", UID); // give each row an id number

// Task checkbox
    var checkbox = document.createElement("input");
    checkbox.setAttribute("type", "checkbox");
    checkbox.setAttribute("class", "taskCheckBox")
    checkbox.setAttribute("onclick", "taskCheck("+UID+")")
    row.appendChild(checkbox);

// User inputed task name
    var newtask = document.createElement("td");
    newtask.setAttribute("class", "taskcolumn");
    newtask.innerHTML = taskName;
    row.appendChild(newtask);

// Add buttons for task operations
    var operations = document.createElement("td");
    operations.setAttribute("class", "opcolumn");
// delete button
    var removeButton = document.createElement("input");
    removeButton.setAttribute("type", "button");
    removeButton.setAttribute("value", "Remove");
    removeButton.setAttribute("class", "removeButton");
    removeButton.setAttribute("onclick", "deleteRow("+UID+")");
// edit button
    var editButton = document.createElement("input");
    editButton.setAttribute("type", "button");
    editButton.setAttribute("value", "Edit");
    editButton.setAttribute("class", "editButton");
    editButton.setAttribute("onclick", "editRow("+UID+")");
// finish up row
    operations.appendChild(editButton);
    operations.innerHTML += " ";
    operations.appendChild(removeButton);
    row.appendChild(operations);

// Clear input field
    document.getElementById('newtask').value = "";
}

function editRow(rowUID) {
// find row
    var task = document.getElementById(rowUID).getElementsByClassName("taskcolumn")[0];

    var oldTask = task.innerHTML;
    task.innerHTML = "";

// replace task with edit field for user to make changes
    var editField = document.createElement("input");
    editField.setAttribute("type", "text");
    editField.setAttribute("value", oldTask);
    editField.setAttribute("class", "editTaskInput");
    editField.setAttribute("onkeydown", "editTaskEnter(this, "+rowUID+")")
    task.appendChild(editField);

// Switch edit button to done button
    var button = document.getElementById(rowUID).getElementsByClassName("editButton")[0];
    button.setAttribute("class", "doneButton");
    button.setAttribute("onclick", "editDone("+rowUID+")");
    button.setAttribute("value", "Done");
}

function editDone(rowUID) {
// find row
    var task = document.getElementById(rowUID).getElementsByClassName("taskcolumn")[0];
// set html to input
    task.innerHTML = task.getElementsByClassName("editTaskInput")[0].value

// switch button from done back to edit
    var button = document.getElementById(rowUID).getElementsByClassName("doneButton")[0];
    button.setAttribute("class", "editButton");
    button.setAttribute("onclick", "editRow("+rowUID+")");
    button.setAttribute("value", "Edit");
}

function deleteRow(rowUID){
    document.getElementById(rowUID).remove();
}

function taskCheck(rowUID) {
    var task = document.getElementById(rowUID).getElementsByClassName("taskcolumn")[0];
    if (task.hasAttribute("style")) {
        task.removeAttribute("style");
    } else {
        task.setAttribute("style", "text-decoration: line-through;");
    }
}

function editTaskEnter(ele, rowUID) {
    if(event.key === 'Enter') {
        event.preventDefault();
        editDone(rowUID);
    }
}

function newTaskEnter(ele) {
    if(event.key === 'Enter') {
        event.preventDefault();
        addTask(document.getElementById('newtask', null).value);
    }
}
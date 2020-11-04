function addTask(taskName) {
    if (taskName.trim() == "") {  // do nothing if input is whitespace or empty
        return;
    }

    var table = document.getElementById("list");
    var UID = Date.now();  // lazy UID for rows
    var row = table.insertRow(-1);

    row.setAttribute("id", UID); // give each row an id number

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

function editRow(rowNumber) {
    var task = document.getElementById(rowNumber).getElementsByClassName("taskcolumn")[0];
    var oldTask = task.innerHTML;
    task.innerHTML = "";

// replace task with edit field for user to make changes
    var editField = document.createElement("input");
    editField.setAttribute("value", oldTask);
    editField.setAttribute("class", "editTaskInput");
    task.appendChild(editField);

// Switch edit button to done button
    var button = document.getElementById(rowNumber).getElementsByClassName("editButton")[0];
    button.setAttribute("class", "doneButton");
    button.setAttribute("onclick", "editDone("+rowNumber+")");
    button.setAttribute("value", "Done");
}

function editDone(rowNumber) {
    var task = document.getElementById(rowNumber).getElementsByClassName("taskcolumn")[0];
// set html to input
    task.innerHTML = task.getElementsByClassName("editTaskInput")[0].value

// switch button from done back to edit
    var button = document.getElementById(rowNumber).getElementsByClassName("doneButton")[0];
    button.setAttribute("class", "editButton");
    button.setAttribute("onclick", "editRow("+rowNumber+")");
    button.setAttribute("value", "Edit");
}

function deleteRow(rowNumber){
    document.getElementById(rowNumber).remove();
}

function editTaskEnter(ele) {
    if(event.key === 'Enter') {
        event.preventDefault();
        editDone();
    }
}

function newTaskEnter(ele) {
    if(event.key === 'Enter') {
        event.preventDefault();
        addTask(document.getElementById('newtask').value);
    }
}
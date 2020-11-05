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

// POST to server to edit by ID
    var xhr = new XMLHttpRequest();
    xhr.open("POST", "/edit", true);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.send(JSON.stringify({
        ID: rowUID,
        task: task.innerHTML
    }));
}

function deleteRow(rowUID){
// remove row in html
    document.getElementById(rowUID).remove();
// POST to server to delete task by ID
    var xhr = new XMLHttpRequest();
    xhr.open("POST", "/remove", true);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.send(JSON.stringify({
        ID: rowUID
    }));
}

function taskCheck(rowUID) {
    var task = document.getElementById(rowUID).getElementsByClassName("taskcolumn")[0];
    var taskComplete = false;
    if (task.hasAttribute("style")) {
        task.removeAttribute("style");
    } else {
        task.setAttribute("style", "text-decoration: line-through;");
        taskComplete = true;
    }
// POST to server to update completed field
    var xhr = new XMLHttpRequest();
    xhr.open("POST", "/complete", true);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.send(JSON.stringify({
        ID: rowUID,
        complete: taskComplete
    }));
}

function editTaskEnter(ele, rowUID) {
    if(event.key === 'Enter') {
        event.preventDefault();
        editDone(rowUID);
    }
}
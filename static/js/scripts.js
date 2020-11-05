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
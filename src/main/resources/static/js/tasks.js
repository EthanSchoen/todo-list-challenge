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
    button.setAttribute("class", "btn btn-secondary doneButton");
    button.setAttribute("onclick", "editDone("+rowUID+")");
    button.setAttribute("value", "Done");
}

function editDone(rowUID) {
// find row
    var task = document.getElementById(rowUID).getElementsByClassName("taskcolumn")[0];

// POST to server to edit by ID
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    var lstId = $("meta[name='listId']").attr("content");
    $.ajax({
        type: "POST",
        url: "/editTask",
        beforeSend: function(request) {
            request.setRequestHeader(header, token);
        },
        contentType: 'application/json',
        data: JSON.stringify( {taskId: rowUID, task: task.getElementsByClassName("editTaskInput")[0].value, listId: lstId} ),
        error: function(data) {
            console.log(data);
        },
        success: function(data){
            window.location.reload();
        }
    });
}

function deleteRow(rowUID){
// POST to server to delete task by ID
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    var lstId = $("meta[name='listId']").attr("content");
    $.ajax({
        type: "POST",
        url: "/removeTask",
        beforeSend: function(request) {
            request.setRequestHeader(header, token);
        },
        contentType: 'application/json',
        data: JSON.stringify( {taskId: rowUID, listId: lstId} ),
        error: function(data) {
            console.log(data);
        },
        success: function(data){
            window.location.reload();
        }
    });
}

function taskCheck(rowUID) {
    var task = document.getElementById(rowUID).getElementsByClassName("taskcolumn")[0];
    var taskComplete = task.hasAttribute("style");
// POST to server to update completed field
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    var lstId = $("meta[name='listId']").attr("content");

    $.ajax({
        type: "POST",
        url: "/completeTask",
        beforeSend: function(request) {
            request.setRequestHeader(header, token);
        },
        contentType: 'application/json',
        data: JSON.stringify( {taskId: rowUID, complete: taskComplete, listId: lstId} ),
        error: function(data) {
            console.log(data);
        },
        success: function(data){
            window.location.reload();
        }
    });
}

function editTaskEnter(ele, rowUID) {
    if(event.key === 'Enter') {
        event.preventDefault();
        editDone(rowUID);
    }
}
function editRow(rowUID) {
    // find row
    var task = $("#"+rowUID+" .taskcolumn");
    var oldTask = task.html();
    // replace task with edit field for user to make changes
    task.html("<input type='text' value='"+oldTask+"' class='editTaskInput' onkeydown='editTaskEnter(this, "+rowUID+")'>");
    // Switch edit button to done button
    var button = $("#"+rowUID+" .opcolumn .editButton");
    button.attr("class", "btn btn-secondary doneButton");
    button.attr("onclick", "editDone("+rowUID+")");
    button.attr("value", "Done");
}

function editDone(rowUID) {
    // find row
    var taskVal = $("#"+rowUID+" .taskcolumn .editTaskInput").val();
    // collect info for POST
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    var lstId = $("meta[name='listId']").attr("content");
    // POST to server to edit by ID
    $.ajax({
        type: "POST",
        url: "/editTask",
        beforeSend: function(request) {
            request.setRequestHeader(header, token);
        },
        contentType: 'application/json',
        data: JSON.stringify( {taskId: rowUID, task: taskVal, listId: lstId} ),
        error: function(data) {
            console.log(data);
        },
        success: function(data){
            window.location.reload();
        }
    });
}

function deleteRow(rowUID){
    // CSRF token
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    var lstId = $("meta[name='listId']").attr("content");
    // POST to server to delete task by ID
    $.ajax({
        type: "POST",
        url: "/removeTask",
        beforeSend: function(request) {
            request.setRequestHeader(header, token);
        },
        contentType: 'application/json',
        data: JSON.stringify({
            taskId: rowUID,
            listId: $("meta[name='listId']").attr("content")
        }),
        error: function(data) {
            console.log(data);
        },
        success: function(data){
            window.location.reload();
        }
    });
}

function taskCheck(rowUID) {
    // CSRF token
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    // POST to server to update completed field
    $.ajax({
        type: "POST",
        url: "/completeTask",
        beforeSend: function(request) {
            request.setRequestHeader(header, token);
        },
        contentType: 'application/json',
        data: JSON.stringify({
            taskId: rowUID,
            complete: $('#'+rowUID+' .taskcolumn').is("[style]"),
            listId: $("meta[name='listId']").attr("content")
        }),
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
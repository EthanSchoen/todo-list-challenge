function removeList(Id) {
    // confirm("This list: " + Id + " contains tasks, are you sure you want to remove it?");

    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $.ajax({
        type: "POST",
        url: "/removeList",
        beforeSend: function(request) {
            request.setRequestHeader(header, token);
        },
        contentType: 'application/json',
        data: JSON.stringify( {listId: Id} ),
        success: function(data){
            window.location.reload();
        }
    });
}
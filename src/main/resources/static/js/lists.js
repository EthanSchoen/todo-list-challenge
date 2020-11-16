function removeList(listID) {
    confirm("This list: " + listID + " contains tasks, are you sure you want to remove it?");

    // var token = $("meta[name='_csrf']").attr("content");
    // var header = $("meta[name='_csrf_header']").attr("content");
    // $.ajax({
    //     type: "POST",
    //     beforeSend: function(request) {
    //         request.setRequestHeader(header, token);
    //     },
    //     url: "/logout",
    //     success: function(data){
    //         window.location.replace("/");
    //     }
    // });
}
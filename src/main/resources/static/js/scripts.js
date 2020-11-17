function logout() {
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $.ajax({
        type: "POST",
        beforeSend: function(request) {
            request.setRequestHeader(header, token);
        },
        url: "/logout",
        success: function(data){
            window.location.replace("/");
        }
    });
    $("#user").html('');
    $(".unauthenticated").show();
    $(".authenticated").hide();
    return true;
}
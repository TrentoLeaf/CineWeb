$(document).ready(function() {

    var button = $('#fab_primary');
    var icon = $('.fixed-button a i');

    button.click(function () {
        if(button.hasClass("active")) {
            button.toggleClass("active");
            $(".fixed-list").removeClass("slideInUp");
            $(".fixed-list").addClass("slideOutDown");
            icon.removeClass("rotateOut");
            icon.addClass("rotateIn");
        } else {
            $(".fixed-list").removeClass("hide-list");
            button.toggleClass("active");
            $(".fixed-list").removeClass("slideOutDown");
            $(".fixed-list").addClass("slideInUp");
            icon.removeClass("rotateIn");
            icon.addClass("rotateOut");
        }
    });
});
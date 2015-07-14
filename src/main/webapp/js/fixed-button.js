$(document).ready(function() {

    var button = $('.fixed-button a');
    var icon = $('.fixed-button a i');

    button.click(function () {
        if(button.hasClass("active")) {
            button.toggleClass("active");
            $(".fixed-list").removeClass("slideInUp");
            $(".fixed-list").addClass("slideOutDown");
            icon.removeClass("rotateOut");
            icon.addClass("rotateIn");
            $(".fixed-list").on('transitionend webkitTransitionEnd oTransitionEnd otransitionend MSTransitionEnd',
                function() {
                    $(".fixed-list").css("display","none");
                });

        } else {
            button.toggleClass("active");
            $(".fixed-list").removeClass("slideOutDown");
            $(".fixed-list").addClass("slideInUp");
            icon.removeClass("rotateIn");
            icon.addClass("rotateOut");
            $(".fixed-list").css("display","inline");
        }
    });
});
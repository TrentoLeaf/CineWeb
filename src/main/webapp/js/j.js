// TODO: serve questo codice???
// se sì -> caricare in index.htmò
// se no -> eliminare!

$(document).ready(function () {

    // init the mobile menu sidenav
    $(".button-collapse").sideNav();

    // init the dropdown selectors
    $('select').material_select();

    // apre i modal di materialize
    $('.modal-trigger').leanModal({         // ANDR REMOVE
        dismissible: true, // Modal can be dismissed by clicking outside of the modal
        opacity: .5, // Opacity of modal background
        in_duration: 400, // Transition in duration
        out_duration: 300, // Transition out duration
        ready: function() { /*alert('Ready');*/ }, // Callback for Modal open
        complete: function() { /*alert('Closed');*/ } // Callback for Modal close
    });


    /* // intercept all clicks
     $('main').on('click.hideSide', function (e) {
     // check if click come from hide on mobile buttons
     if ($(e.target).hasClass('hide-side-nav-button-mobile')) {
     // hide the side-div
     close_Sidediv();
     }

     if (!$(e.target).hasClass('side-div')) {
     // check if click not come from the Sidediv
     if (!($(e.target).parents().hasClass('side-div'))) {
     // hide the side-div
     close_Sidediv();
     }
     }
     });*/

});

function close_Sidediv() {
    $('.side-div').removeClass('side-div-w');
    $('.side-div').find('.side-nav-element').addClass('ng-hide');
}
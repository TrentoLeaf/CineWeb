(function() {
    "use strict";

    angular.module('PlaysModule', []).controller('PlaysController', function () {
        this.archive = [
            {
                title : "Questo è il titolo 1",
                title2 : "Questo è il titolo 2",
                playbill : "img/temporary/images.jpg",
                description : "Questa è una descrizione",
            },{
                title : "Questo è il asdf 1",
                title2 : "Questo è il asf 2",
                playbill : "img/temporary/images.jpg",
                description : "Questa è asgsdasd descrizione",
            }
        ];
    });
})();
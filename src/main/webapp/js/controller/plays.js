(function() {
    "use strict";

    angular.module('PlaysModule', []).controller('PlaysController', function () {
        this.archive = [
            {
                title : "Titolo 1",
                time : "Orario 1",
                gender: "Genere 1",
                playbill : "img/temporary/images.jpg",
                description : "Descrizione 1",
            },{
                title : "Titolo 2",
                time : "Orario 2",
                gender : "Genere 2",
                playbill : "img/temporary/terminator_genisys_ver9-201x298.jpg",
                description : "Descrizione 2",
            },{
                title : "Titolo 3",
                time : "Orario 3",
                gender: "Genere 3",
                playbill : "img/temporary/images.jpg",
                description : "Descrizione 3",
            },{
                title : "Titolo 4",
                time : "Orario 4",
                gender : "Genere 4",
                playbill : "img/temporary/terminator_genisys_ver9-201x298.jpg",
                description : "Descrizione 4",
            },{
                title : "Titolo 5",
                time : "Orario 5",
                gender: "Genere 5",
                playbill : "img/temporary/images.jpg",
                description : "Descrizione 5",
            },{
                title : "Titolo 6",
                time : "Orario 6",
                gender : "Genere 6",
                playbill : "img/temporary/terminator_genisys_ver9-201x298.jpg",
                description : "Descrizione 6",
            }
        ];
    });
})();
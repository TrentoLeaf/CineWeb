(function () {
    'use strict';

    angular.module('adminRooms', ['filmsPlaysModule'])

// TODO mancano le dipendenze dal servizio Room (che non esiste)
        .controller('AdminRoomsController', ['$rootScope', '$location', function ($rootScope, $location) {

            var ctrl = this;
            this.tmpRoom = {};
            this.rooms = [];
            this.shared_obj = {};

            var init = function () {
                if ($rootScope.isUserLogged == false) {
                    $rootScope.afterLogin = "userArea";
                    $location.path('/login');
                }

                ctrl.rooms = [];
            };

            this.loadPlay = function () {
                init();
               // TODO request al server tramite il servizio Room per ottenere tutte le sale
            };

            this.setCurrentRoom = function (index) {
                // TODO rimuovere il commento qui sotto
                //ctrl.tmpRoom = ctrl.rooms[index];
                // TODO recuperare la matrice della sala e metterla in ctrl.tmpRoom.theatreMap
                //ctrl.tmpRoom.theatreMap = undefined;


                if (ctrl.tmpRoom.theatreMap == undefined) {
                    ctrl.shared_obj.mapTheatre = [];
                } else {
                    ctrl.shared_obj.mapTheatre = ctrl.tmpRoom.theatreMap;
                }

                // TODO selezionare la sala ($(elemento-giusto).addClass('admin-elem-active');), e togliere la vecchia selezione

            };


            this.loadPlay();

        }]);


})();
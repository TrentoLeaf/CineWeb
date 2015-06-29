(function () {
    'use strict';

    angular.module('adminRooms', ['filmsPlaysModule'])

// TODO mancano le dipendenze dal servizio Room (che non esiste)
        .controller('AdminRoomsController', ['$rootScope', '$location', 'Rooms', function ($rootScope, $location, Rooms) {

            var ctrl = this;
            this.currentRoom = {};
            this.rooms = [];
            this.shared_obj = {};
            this.error = "";

            var init = function () {
                if ($rootScope.isUserLogged == false) {
                    $rootScope.afterLogin = "userArea";
                    $location.path('/login');
                }

                ctrl.rooms = [];
            };

            this.loadRooms = function () {
                init();
                // TODO request al server tramite il servizio Room per ottenere tutte le sale
                Rooms.getRoomsOnly()
                    .success(function (data) {
                        ctrl.rooms = data;
                    })
                    .error(function (error) {
                        ctrl.error="Sale non caricate.";
                    });

            };

            this.setCurrentRoom = function (index) {
                ctrl.currentRoom = ctrl.rooms[index];
                // TODO selezionare la sala ($(elemento-giusto).addClass('admin-elem-active');), e togliere la vecchia selezione


                Rooms.getRoomByID(ctrl.currentRoom.rid)
                    .success(function (data) {
                        ctrl.shared_obj.mapTheatre = data.seats;
                    })
                    .error (function (error) {
                    ctrl.shared_obj.mapTheatre = [];
                });


            };


            this.loadRooms();

        }]);


})();
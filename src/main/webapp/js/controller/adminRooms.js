(function () {
    'use strict';

    angular.module('adminRooms', ['filmsPlaysModule'])
        .controller ('AdminNewRoomController', ['$rootScope', '$location', 'Rooms', function ($rootScope, $location, Rooms) {

        var ctrl = this;
        this.newRoom = {};
        this.shared_obj = {};
        this.matrix = [];

        this.createMatrix = function(rows, columns) {
            for(var i=0; i<rows; i++) {
                ctrl.matrix[i] = [];
                for(var j=0; j<columns; j++) {
                    ctrl.matrix[i][j] = 1;
                }
            }
        };

        this.generateMap = function () {
            ctrl.createMatrix(ctrl.newRoom.rows, ctrl.newRoom.columns);
            ctrl.newRoom.seats = ctrl.matrix;
            ctrl.matrix = [];
            ctrl.shared_obj.mapTheatre = ctrl.newRoom.seats;
        };

    }])

// TODO mancano le dipendenze dal servizio Room (che non esiste)
        .controller('AdminRoomsController', ['$rootScope', '$location', 'Rooms', function ($rootScope, $location, Rooms) {

            var ctrl = this;
            this.currentRoom = {};
            this.newRoom = {};
            this.newRoom.seats = [ctrl.newRoom.rows][ctrl.newRoom.columns];
            this.rooms = [];
            this.shared_obj = {};
            this.error = "";

            var init = function () {
                console.log(ctrl.newRoom);
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
                        console.log(data);
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
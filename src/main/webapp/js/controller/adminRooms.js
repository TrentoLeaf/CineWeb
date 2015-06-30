(function () {
    'use strict';

    angular.module('adminRooms', ['filmsPlaysModule'])
        .controller ('AdminNewRoomController', ['$rootScope', '$location', 'Rooms', 'Theatre', function ($rootScope, $location, Rooms, Theatre) {

        var ctrl = this;
        this.newRoom = {};
        this.shared_obj = {};
        this.matrix = [];
        this.hiddenSeats = [];

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
            // set parameters
            ctrl.shared_obj.editable = true;
            ctrl.shared_obj.selected_seats = [];
            ctrl.shared_obj.mapTheatre = ctrl.newRoom.seats;
        };

        this.sendMap = function () {
            ctrl.hiddenSeats = ctrl.shared_obj.selected_seats;

            // mette a 0 (non esistenti) in ctrl.newRoom.seats le poltrone che sono state selezionate (cioè quelle non esistenti)
            for (var seat = 0; seat < ctrl.hiddenSeats.length; seat++) {
                var row = parseInt(ctrl.hiddenSeats[seat].row);
                var col = parseInt(ctrl.hiddenSeats[seat].col);
                ctrl.newRoom.seats[row][col] = 0;
            }

            ctrl.newRoom.rows = ctrl.newRoom.seats.length;
            ctrl.newRoom.columns = ctrl.newRoom.seats[0].length;


            // TODO inviare al server ctrl.newRoom (#domandona: chi lo sceglie il rid?)
            Theatre.addRoom(ctrl.newRoom);
        };


    }])

        .controller('AdminRoomsController', ['$rootScope', '$location', 'Rooms', function ($rootScope, $location, Rooms) {

            var ctrl = this;
            this.currentRoom = {};
            this.newRoom = {};
            this.newRoom.seats = [ctrl.newRoom.rows][ctrl.newRoom.columns];
            this.rooms = [];
            this.currentSelected = -1;
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
                ctrl.currentSelected = index;
                Rooms.getRoomByID(ctrl.currentRoom.rid)
                    .success(function (data) {
                        ctrl.shared_obj.editable = false;
                        ctrl.shared_obj.mapTheatre = data.seats;
                    })
                    .error (function (error) {
                    ctrl.shared_obj.editable = false;
                    ctrl.shared_obj.mapTheatre = [];
                });


            };

            this.isSelected = function (index) {
              return (ctrl.currentSelected == index);
            };

            this.loadRooms();

        }]);


})();
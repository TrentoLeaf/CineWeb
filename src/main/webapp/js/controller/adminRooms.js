(function () {
    'use strict';

    angular.module('adminRooms', ['filmsPlaysModule'])
        .controller ('AdminNewRoomController', ['$rootScope', '$location', 'Rooms', 'Theatre', function ($rootScope, $location, Rooms, Theatre) {

        var ctrl = this;
        this.newRoom = new Theatre();
        this.shared_obj = {};
        this.matrix = [];
        this.hiddenSeats = [];
        this.error_msg = "";

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

            $(".room-loader").addClass("active");
            ctrl.error_msg = "";
            ctrl.hiddenSeats = ctrl.shared_obj.selected_seats;

            if (ctrl.hiddenSeats != undefined) {
                // mette a 0 (non esistenti) in ctrl.newRoom.seats le poltrone che sono state selezionate (cioè quelle non esistenti)
                for (var seat = 0; seat < ctrl.hiddenSeats.length; seat++) {
                    var row = parseInt(ctrl.hiddenSeats[seat].row);
                    var col = parseInt(ctrl.hiddenSeats[seat].col);
                    ctrl.newRoom.seats[row][col] = 0;
                }

                if ((ctrl.newRoom.seats.length != undefined) && (ctrl.newRoom.seats[0].length != undefined)) {
                    ctrl.newRoom.rows = ctrl.newRoom.seats.length;
                    ctrl.newRoom.columns = ctrl.newRoom.seats[0].length;
                }

                ctrl.newRoom.$save(function (data) {
                    $(".room-loader").removeClass("active");
                    console.log("Theatre add success");
                    $location.path('/admin/rooms');
                }, function () {
                    $(".room-loader").removeClass("active");
                    ctrl.error_msg = "Aggiunta della nuova sala fallita.";
                    console.log("Theatre add fail");
                });
            } else {
                $(".room-loader").removeClass("active");
                ctrl.error_msg = "Ricontrolla i dati inseriti.";
            }
        };

    }])

        .controller ('AdminEditRoomController', ['$rootScope', '$routeParams', '$location', 'Rooms', 'Theatre', function ($rootScope, $routeParams, $location, Rooms, Theatre) {

        var ctrl = this;
        this.rid = $routeParams.rid;
        this.rows = 0;
        this.cols = 0;
        this.matrix = [[]];
        this.shared_obj = {};
        this.hiddenSeats = [];
        this.error_msg = "";

        this.getRoomMap = function () {
            // TODO richiedre al server la matrice da visualizzare
            // callback
            //matrix =data;
            // importante: set rows e cols prima di genmap
            //generateMap();
        };


        this.generateMap = function () {

            // set parameters of shared_obj
            ctrl.shared_obj.editable = true;
            ctrl.shared_obj.selected_seats = []; // tha map directive will fill the array
            ctrl.shared_obj.mapTheatre = matrix;

        };

        this.generateNewMatrix = function () {
            /*
             genera una nuova matrice di righe ctrl.rows e colonne ctrl.cols rispettando però i vincoli
             dati dalla matrice originaria recuperata dal server
             */
            // todo fare...ma anche no
        };

        this.sendMap = function () {
            $(".room-loader").addClass("active");
            ctrl.hiddenSeats = ctrl.shared_obj.selected_seats;

            // mette a 0 (non esistenti) in ctrl.newRoom.seats le poltrone che sono state selezionate (cioè quelle non esistenti)
            for (var seat = 0; seat < ctrl.hiddenSeats.length; seat++) {
                var row = parseInt(ctrl.hiddenSeats[seat].row);
                var col = parseInt(ctrl.hiddenSeats[seat].col);
                ctrl.matrix[row][col] = 0;
            }

            var rows = ctrl.matrix.length;
            var cols = ctrl.matrix[0].length;


            //  TODO chiamata al giusto servizio per aggiungere la mappa modificata
            //callback
            // $(".room-loader").removeClass("active");
            // $location.path("/admin/rooms");
            //error
            // $(".room-loader").removeClass("active");
            // set error_msg




            /*ctrl.newRoom.$save(function (data) {
             console.log("Theatre add success");
             ctrl.updateRoom();
             $location.path('/admin/rooms');
             }, function () {
             console.log("Theatre add fail");
             });*/
        };


        ctrl.getRoomMap();
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
                Rooms.getRoomTopByID(ctrl.currentRoom.rid)
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
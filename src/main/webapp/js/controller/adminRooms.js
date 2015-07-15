(function () {
    'use strict';

    /* modulo per la gestione delle sale */
    angular.module('adminRooms', ['filmsPlaysModule'])
        /* controller per una nuova sala */
        .controller ('AdminNewRoomController', ['$rootScope', '$location', 'Rooms', 'Theatre', function ($rootScope, $location, Rooms, Theatre) {

        var ctrl = this;
        this.newRoom = new Theatre();
        this.shared_obj = {};
        this.matrix = [];
        this.hiddenSeats = [];
        this.error_msg = "";

        // crea la matrice dei posti (tutti disponibili)
        this.createMatrix = function(rows, columns) {
            for(var i=0; i<rows; i++) {
                ctrl.matrix[i] = [];
                for(var j=0; j<columns; j++) {
                    ctrl.matrix[i][j] = 1;
                }
            }
        };

        // genera e renderizza la sala
        this.generateMap = function () {

            ctrl.createMatrix(ctrl.newRoom.rows, ctrl.newRoom.columns);
            ctrl.newRoom.seats = ctrl.matrix;
            ctrl.matrix = [];
            // set parameters
            ctrl.shared_obj.editable = true;
            ctrl.shared_obj.selected_seats = [];
            ctrl.shared_obj.mapTheatre = ctrl.newRoom.seats;
        };

        // invia la nuova sala al server
        this.sendMap = function () {

            $(".room-loader").addClass("active");
            ctrl.error_msg = "";
            ctrl.hiddenSeats = ctrl.shared_obj.selected_seats; // array poltrone eliminate dall'admin

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

                // invia richiesta
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
        /* controller per la modifica di una sala */
        .controller ('AdminEditRoomController', ['$rootScope', '$routeParams', '$location', 'Rooms', 'Theatre', function ($rootScope, $routeParams, $location, Rooms, Theatre) {

        var ctrl = this;
        this.rid = $routeParams.rid;
        this.rows = 0;
        this.cols = 0;
        this.matrix = [[]];
        this.shared_obj = {};
        this.hiddenSeats = [];
        this.error_msg = "";
        // variabile che indica se una mappa di una sala è stata caricata
        this.mapLoaded = false;

        this.getRoomMap = function () {
            // inizializza variabile 'caricamento mappa completato'
            ctrl.mapLoaded = false;
            ctrl.error_msg = "";

            // richiesta dati sala
            Rooms.getRoomEditableByID(ctrl.rid)
                .success(function (data) {
                    ctrl.matrix = data.seats;
                    ctrl.rows = ctrl.matrix.length;
                    ctrl.cols = ctrl.matrix[0].length;
                    ctrl.generateMap();
                    // imposta variabile 'caricamento mappa completato'
                    ctrl.mapLoaded = true;
                })
                .error(function () {
                    ctrl.error_msg = "Non è stato possibile scaricare la mappa dal server.";
                    // imposta variabile 'caricamento mappa completato'
                    ctrl.mapLoaded = true;
                });
        };

        // renderizza la sala
        this.generateMap = function () {

            // set parameters of shared_obj
            ctrl.shared_obj.editable = true;
            ctrl.shared_obj.selected_seats = []; // tha map directive will fill the array
            ctrl.shared_obj.mapTheatre = ctrl.matrix;
        };

        // invia la sala modificata
        this.sendMap = function () {
            ctrl.error_msg = "";
            var rows = ctrl.matrix.length;
            var cols = ctrl.matrix[0].length;

            $(".room-loader").addClass("active");
            ctrl.hiddenSeats = ctrl.shared_obj.selected_seats;  // array di poltrone non esistenti

            // sovrascrive la mappa con una nuova pulita (tutte poltrone disponibili)
            ctrl.matrix = ctrl.createMatrix(rows, cols);

            // mette a 0 (non esistenti) in ctrl.newRoom.seats le poltrone che sono state selezionate (cioè quelle non esistenti)
            for (var seat = 0; seat < ctrl.hiddenSeats.length; seat++) {
                var row = parseInt(ctrl.hiddenSeats[seat].row);
                var col = parseInt(ctrl.hiddenSeats[seat].col);
                ctrl.matrix[row][col] = 0;
            }

            var obj = {
                rows: rows,
                columns: cols,
                seats: ctrl.matrix
            };

            // invio richiesta di modifica
            Rooms.editRoom(ctrl.rid, obj)
                .success(function () {
                    $(".room-loader").removeClass("active");
                    $location.path("/admin/rooms");
                })
                .error(function (status) {
                    $(".room-loader").removeClass("active");
                    if (status == 409) {
                        ctrl.error_msg = "Si sta cercando di modificare posti già prenotati.";
                    } else {
                        ctrl.error_msg = "Modifica della sala fallita.";
                    }
                });
        };

        // crea la matrice dei posti
        this.createMatrix = function(rows, columns) {
            var matrix = [];
            for(var i=0; i<rows; i++) {
                matrix[i] = [];
                for(var j=0; j<columns; j++) {
                    matrix[i][j] = 1;
                }
            }
            return matrix;
        };


        ctrl.getRoomMap();
    }])
        /* controller per la visualizzazione delle sale e dei posti migliori per ciascuna sala */
        .controller('AdminRoomsController', ['$rootScope', '$location', 'Rooms', function ($rootScope, $location, Rooms) {

            var ctrl = this;
            this.currentRoom = {};
            this.newRoom = {};
            this.newRoom.seats = [ctrl.newRoom.rows][ctrl.newRoom.columns];
            this.rooms = [];
            this.currentSelected = -1;
            this.shared_obj = {};
            this.error = "";
            // variabile che indica se una mappa di una sala è stata caricata
            this.mapLoaded = true;

            // carica tutti i dati di base delle sale
            this.loadRooms = function () {
                ctrl.rooms = [];
                Rooms.getRoomsOnly()
                    .success(function (data) {
                        console.log(data);
                        ctrl.rooms = data;
                    })
                    .error(function (error) {
                        ctrl.error="Sale non caricate.";
                    });
            };

            // recupera la matrice dei posti per una sala e i posti migliori e la renderizza
            this.setCurrentRoom = function (index) {
                // inizializza variabile 'caricamento mappa completato'
                ctrl.mapLoaded = false;
                ctrl.currentRoom = ctrl.rooms[index];
                ctrl.currentSelected = index;
                Rooms.getRoomTopByID(ctrl.currentRoom.rid)
                    .success(function (data) {
                        ctrl.shared_obj.editable = false;
                        ctrl.shared_obj.mapTheatre = data.seats;
                        // imposta variabile 'caricamento mappa completato'
                        ctrl.mapLoaded = true;
                    })
                    .error (function (error) {
                    ctrl.shared_obj.editable = false;
                    ctrl.shared_obj.mapTheatre = [];
                    // imposta variabile 'caricamento mappa completato'
                    ctrl.mapLoaded = true;
                });
            };

            this.loadRooms();
        }]);
})();
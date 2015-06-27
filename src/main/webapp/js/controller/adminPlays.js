(function () {
    'use strict';

    angular.module('adminPlays', ['filmsPlaysModule'])

        .controller('AdminPlaysEditController', ['$routeParams', '$location', 'Plays', function($routeParams, $location, Plays) {

            var ctrl = this;
            this.currentPlay = {};

            Plays.get({id:$routeParams.uid}).$promise.then(function (data) {
                ctrl.currentPlay = data;
            }, function () {
                $location.path("/admin/plays");
            });


            this.save = function () {
                Plays.update({id: ctrl.currentPlay.pid}, ctrl.currentPlay).$promise.then(function (data) {
                    // ok
                    console.log("UPDATE OK ->");
                    console.log(data);
                    $location.path("/admin/plays")
                }, function () {
                    // fail...
                    console.log("UPDATE fail");
                });
            };


        }])
        .controller('AdminPlaysController', ['$location', 'Plays', function ($location, Plays) {


            var ctrl = this;
            this.newPlay = new Plays();
            this.tmpPlay = {};
            this.newPlay_3d = false;
            this.plays = [];
            this.shared_obj = {};

            var init = function () {

                ctrl.loading = true;
                ctrl.plays = [];
                ctrl.tmpPlay = {};
            };

            this.loadPlay = function () {

                init();
                Plays.query(function (data) {
                    ctrl.plays = data;
                    ctrl.loading = false;
                    console.log("PLAYS DOWNLOADED");
                    console.log(data);
                }, function (){
                    //Fail Case
                });
            };

            this.setCurrentPlay = function (index) {
                // TODO rimuovere il commento qui sotto
                //ctrl.tmpPlay = ctrl.plays[index];
                // TODO recuperare la matrice della sala e metterla in ctrl.tmpPlay.theatreMap
                //ctrl.tmpPlay.theatreMap = undefined;


                if (ctrl.tmpPlay.theatreMap == undefined) {
                    ctrl.shared_obj.mapTheatre = [];
                } else {
                    ctrl.shared_obj.mapTheatre = ctrl.tmpPlay.theatreMap;
                }

                // TODO selezionare la proiezione ($(elemento-giusto).addClass('admin-elem-active');), e togliere la vecchia selezione
            };

            this.addPlay = function (data) {
                if (ctrl.newPlay_3d) {
                    ctrl.newPlay._3d = true;
                } else {
                    ctrl.newPlay._3d = false;
                };

                ctrl.newPlay.$save( function (data) {
                    ctrl.plays.push(data);
                    ctrl.newPlay = new Films();
                    console.log("Play insertion success");
                    $location.path("/admin/plays");

                }, function () {
                    //Fail Case
                    console.log("Play insertion fail");
                });
            };

            this.open_delete_modal = function (index) {
                $('#modal_deleteAgree').openModal();
                ctrl.tmpPlay = ctrl.plays[index];
            };

            this.open_modify_page = function (index) {
                // TODO change modal
                $('#modal_deleteAgree').openModal();
                ctrl.tmpPlay = ctrl.plays[index];
                $location.path('/admin/plays/' + ctrl.tmpPlay.pid);
            };


            this.loadPlay();

        }]);


})();
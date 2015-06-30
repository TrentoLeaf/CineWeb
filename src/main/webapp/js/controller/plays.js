(function () {
    "use strict";

    angular.module('PlaysModule', ['filmsPlaysModule'])
        .controller('PlaysController', ['$rootScope', '$location', 'CompletePlays','$sce', function ($rootScope, $location, CompletePlays, $sce) {

            var ctrl = this;
            this.current = {};
            this.show_trailer_for_current = "Trailer";

            this.isNow = function (date) {
                var act = new Date();
                console.log("This is the date: " + date + " and this is the actual: " + act);
                return true;
            };

            this.setCurrent = function (date, film) {
                this.current = $rootScope.playsByDate[date].films[film];
                $('#modal').openModal();
            };

            this.trustSrc = function(src) {
                if (src != undefined) {
                    src = src.replace("watch?v=", "v/");
                }
                return $sce.trustAsResourceUrl(src);
            };

            this.closeWindow = function () {
                console.log("Close window...");
                $('#modal').closeModal();
            };

            this.showTrailer = function () {
                if (this.show_trailer_for_current == "Trailer") {
                    this.show_trailer_for_current = "Locandina";
                } else {
                    this.show_trailer_for_current = "Trailer";
                }
            };

            this.log = function () {
                console.log("pllog");
            };

        }]);
})();
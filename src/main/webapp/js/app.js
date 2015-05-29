(function() {
  'use strict';

  var app= angular.module('Main', ['PlaysModule']);
  app.controller('TabController', function() {
    this.tab = 1;
    this.setTab = function (tab) {
      this.tab = tab;
    };
    this.isSet = function (Value) {
      return this.tab === Value;
    };
  });
})();
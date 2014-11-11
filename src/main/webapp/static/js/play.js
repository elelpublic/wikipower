angular.module('docsIsoFnBindExample', [])
.controller('Controller', ['$scope', '$timeout', function($scope, $timeout) {
  $scope.name = 'Tobias';
  $scope.hideDialog = function () {
    $scope.dialogIsHidden = true;
    $timeout(function () {
      $scope.dialogIsHidden = false;
      console.log( 'Hello' );
    }, 2000);
  };
}])
.controller('TableController', ['$scope', function($scope) {
  $scope.rows = [
    {"c1": "C1", "c2": "C2", "c3": "C3", "c4": "C4"},
    {"c1": "C11", "c2": "C22", "c3": "C33", "c4": "C44"},
    {"c1": "C111", "c2": "C222", "c3": "C333", "c4": "C444"},
    {"c1": "C1111", "c2": "C2222", "c3": "C3333", "c4": "C4444"}
  ];
}])
.directive('myDialog', function() {
  return {
    restrict: 'E',
    transclude: true,
    scope: {
      'close': '&onClose'
    },
    templateUrl: 'templates/my-customer.html'
  };
})
.directive('bsmTable', function() {
  return {
    restrict: 'E',
    templateUrl: 'templates/bsm-table.html'
  };
});
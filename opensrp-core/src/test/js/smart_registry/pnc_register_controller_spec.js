describe("PNC Register controller", function () {

    var controller, scope, pncService, ancService;

    beforeEach(module("smartRegistry.controllers"));
    beforeEach(module("smartRegistry.services"));
    beforeEach(module("smartRegistry.filters"));
    beforeEach(inject(function ($controller, $rootScope, SmartHelper, ANCService,  PNCService) {
        scope = $rootScope.$new();
        controller = $controller("pncRegisterController", {
            $scope: scope,
            ancService: ANCService,
            pncService: PNCService
        });
    }));

    describe("Days Post-partum", function () {
        it("should return zero days pp on the day of delivery", function () {
            var deliveryDate = "2013-01-14";
            scope.getToday = function() {
                return new Date(Date.parse("Jan 14, 2013 22:10:00 GMT+0530"));
            };
            var client = {
              deliveryDate: deliveryDate
            };
            expect(scope.daysPP(client)).toBe(0);
        });

        it("should return one day pp on the next day of delivery", function () {
            var deliveryDate = "2013-01-13";
            scope.getToday = function() {
                return new Date(Date.parse("Jan 14, 2013 22:10:00 GMT+0530"));
            };
            var client = {
              deliveryDate: deliveryDate
            };
            expect(scope.daysPP(client)).toBe(1);
        })
    });
});

describe("ANC Register controller: ", function () {

    var controller, scope;

    beforeEach(module("smartRegistry.controllers"));
    beforeEach(module("smartRegistry.services"));
    beforeEach(module("smartRegistry.filters"));
    beforeEach(inject(function ($controller, $rootScope) {
        scope = $rootScope.$new();
        controller = $controller("ancRegisterController", {
            $scope: scope
        });
    }));

    it("should load clients from service", function () {
        expect(scope.clients.length).toBeGreaterThan(0);
    });

    it("should sum the number of tablets from ifa data", function () {
        var ifaData = {
            next: {
                name: "ifa2",
                status: "upcoming",
                visit_date: null
            },
            IFA: [
                {
                    status: "done",
                    visit_date: "04/04",
                    data: {
                        dose: 100
                    }
                },
                {
                    status: "done",
                    visit_date: "04/04",
                    data: {
                        dose: 120
                    }
                }
            ],
            previous: {}
        };
        var tabletsGiven = scope.sumIFATablets(ifaData);
        expect(tabletsGiven).toEqual(220);
    });

    describe("defaultVillageFilterOptions", function () {
        it("should default village filter options", function () {
            var expectedDefaultVillageOptions = {
                type: "filterVillage",
                options: [
                    {
                        label: "All",
                        id: "",
                        handler: "filterByInAreaLocationStatus"
                    },
                    {
                        label: "O/A",
                        id: "out_of_area",
                        handler: "filterByLocationStatus"
                    }
                ]
            };

            expect(JSON.stringify(scope.defaultVillageOptions)).toBe(JSON.stringify(expectedDefaultVillageOptions));
        });
    });

    describe("Weeks Pregnant", function () {
        it("calculates weeks pregnant from client"s lmp", function () {
            var today = new Date();
            var a_week_ago = new Date(today.getFullYear(), today.getMonth(), today.getDate() - 7);
            var month = ("0" + (a_week_ago.getMonth() + 1)).substr(-2);
            var day = ("0" + a_week_ago.getDate()).substr(-2);
            var client = {
                lmp: a_week_ago.getFullYear() + "-" + month + "-" + day
            };

            expect(scope.weeksPregnant(client)).toEqual(1);
        });
    });

    describe("HB Level Legend", function () {
        it("should return hb-legend-dangerous id level < 7", function () {
            var level = 5;
            expect(scope.hbLegendClass(level)).toEqual("hb-legend-dangerous");
        });

        it("should return hb-legend-high id level >= 7 and < 11", function () {
            var level = 10;
            expect(scope.hbLegendClass(level)).toEqual("hb-legend-high");
        });

        it("should return hb-legend-normal id level >= 11", function () {
            var level = 12;
            expect(scope.hbLegendClass(level)).toEqual("hb-legend-normal");
        });
    });

});

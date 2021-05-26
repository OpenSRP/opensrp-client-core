describe("PNC Service", function () {
    var pncService, smartHelper;

    beforeEach(module("smartRegistry.services"));
    beforeEach(inject(function (PNCService, SmartHelper) {
        pncService = PNCService;
        smartHelper = SmartHelper;
    }));

    describe("Pre-process PNC client data", function () {
        it("should properly calculate the expected visits day/date values", function(){
            var client = {
                deliveryDate: "2013-05-13"
            };

            var calculatedVisitData = [
                {
                    day: 1,
                    date: "2013-05-14"
                },
                {
                    day: 3,
                    date: "2013-05-16"
                },
                {
                    day: 7,
                    date: "2013-05-20"
                }
            ];

            // set the current date to 8 days after delivery
            current_date = new Date("2013-05-21");

            var expectedData = pncService.calculateExpectedVisitDates(client, current_date);
            expect(calculatedVisitData).toEqual(expectedData);
        });

        describe("First 7 days visit calculation", function(){
            it("should generate a list of visits within the first 7 days sorted by visit date", function(){
                var delivery_date = new Date("2013-05-13");

                var services_provided = [
                    {
                        name: "PNC",
                        date: "2013-05-26"
                    },
                    {
                        name: "PNC",
                        date: "2013-05-15"
                    },
                    {
                        name: "PNC",
                        date: "2013-05-14"
                    },
                    {
                        name: "PNC",
                        date: "2013-05-17"
                    },
                    {
                        name: "PNC",
                        date: "2013-05-20"
                    }
                ];

                var expected_visits = [
                    {
                        name: "PNC",
                        day: 1,
                        date: "2013-05-14"
                    },
                    {
                        name: "PNC",
                        day: 2,
                        date: "2013-05-15"
                    },
                    {
                        name: "PNC",
                        day: 4,
                        date: "2013-05-17"
                    },
                    {
                        name: "PNC",
                        day: 7,
                        date: "2013-05-20"
                    }
                ];

                var first_7_days = pncService.getFirst7DaysVisits(delivery_date, services_provided);

                expect(first_7_days.length).toEqual(expected_visits.length);
                expect(first_7_days[0]).toEqual(expected_visits[0]);
                expect(first_7_days[1]).toEqual(expected_visits[1]);
                expect(first_7_days[2]).toEqual(expected_visits[2]);
            });

            it("should only return visits that are within the 7 day period", function(){
                var services_provided = [
                    {
                        name: "PNC",
                        date: "2013-05-26"
                    },
                    {
                        name: "PNC",
                        date: "2013-05-14"
                    }
                ];

                // TODO: test that days are set appropriately based on offest from delivery date
                var expected_visits = [
                    {
                        name: "PNC",
                        day: 1,
                        date: "2013-05-14"
                    }
                ];

                var delivery_date = new Date("2013-05-13");

                var first_3_visits = pncService.getFirst7DaysVisits(delivery_date, services_provided);

                expect(first_3_visits.length).toEqual(expected_visits.length);
                expect(first_3_visits[0]).toEqual(expected_visits[0]);
            });

            it("should return unique day values and ignore duplicate visits on the same date", function(){
                var services_provided = [
                    {
                        name: "PNC",
                        date: "2013-05-20"
                    },
                    {
                        name: "PNC",
                        date: "2013-05-16"
                    },
                    {
                        name: "PNC",
                        date: "2013-05-16"
                    },
                    {
                        name: "PNC",
                        date: "2013-05-14"
                    }
                ];

                // TODO: test that days are set appropriately based on offest from delivery date
                var expected_visits = [
                    {
                        name: "PNC",
                        day: 1,
                        date: "2013-05-14"
                    },
                    {
                        name: "PNC",
                        day: 3,
                        date: "2013-05-16"
                    },
                    {
                        name: "PNC",
                        day: 7,
                        date: "2013-05-20"
                    }
                ];

                var delivery_date = new Date("2013-05-13");

                var first_3_visits = pncService.getFirst7DaysVisits(delivery_date, services_provided);

                expect(first_3_visits.length).toEqual(expected_visits.length);
                expect(first_3_visits[0]).toEqual(expected_visits[0]);
                expect(first_3_visits[1]).toEqual(expected_visits[1]);
                expect(first_3_visits[2]).toEqual(expected_visits[2]);
            });
        });

        describe("Current date is delivery date", function(){
            var client = {
                deliveryDate:"2013-05-13",
                services_provided:[]
            };

            var expected_visit_data;

            // set the current date to the day of delivery
            var current_date = new Date("2013-05-13");

            beforeEach(function(){
                // initialise first 7 days data
                client.visits = {first_7_days: {}};

                expected_visit_data = pncService.calculateExpectedVisitDates(client);
            });

            it("should create grey circles for each of the expected days", function () {
                /// TODO: test color based on current date
                var expected_circles = [
                    {
                        day: 1,
                        type: "expected",
                        colored: false
                    },
                    {
                        day: 3,
                        type: "expected",
                        colored: false
                    },
                    {
                        day: 7,
                        type: "expected",
                        colored: false
                    }
                ];

                pncService.preProcessFirst7Days(client, expected_visit_data, current_date);

                /// check circles
                expect(client.visits.first_7_days.circles.length).toEqual(expected_circles.length);
                expect(client.visits.first_7_days.circles[0]).toEqual(expected_circles[0]);
                expect(client.visits.first_7_days.circles[1]).toEqual(expected_circles[1]);
                expect(client.visits.first_7_days.circles[2]).toEqual(expected_circles[2]);
            });

            it("should not create any statuses", function(){
                pncService.preProcessFirst7Days(client, expected_visit_data, current_date);
                /// check icons
                expect(client.visits.first_7_days.statuses.length).toEqual(0);
            });

            it("should set active color to green", function(){
                pncService.preProcessFirst7Days(client, expected_visit_data, current_date);

                /// check active color
                expect(client.visits.first_7_days.active_color).toEqual("green");
            });

            it("should create grey ticks for each of the tick days", function () {
                var expected_ticks = [
                    {
                        day: 2,
                        type: "expected"
                    },
                    {
                        day: 4,
                        type: "expected"
                    },
                    {
                        day: 5,
                        type: "expected"
                    },
                    {
                        day: 6,
                        type: "expected"
                    }
                ];

                pncService.preProcessFirst7Days(client, expected_visit_data, current_date);

                /// check ticks
                expect(client.visits.first_7_days.ticks.length).toEqual(expected_ticks.length);
                expect(client.visits.first_7_days.ticks[0]).toEqual(expected_ticks[0]);
                expect(client.visits.first_7_days.ticks[1]).toEqual(expected_ticks[1]);
                expect(client.visits.first_7_days.ticks[2]).toEqual(expected_ticks[2]);
                expect(client.visits.first_7_days.ticks[3]).toEqual(expected_ticks[3]);
            });

            it("should create a line from day 1 to day 7", function () {
                var expected_lines = [
                    {
                        start: 1,
                        end: 7,
                        type: "expected"
                    }
                ];

                pncService.preProcessFirst7Days(client, expected_visit_data, current_date);

                /// check lines
                expect(client.visits.first_7_days.lines.length).toEqual(expected_lines.length);
                expect(client.visits.first_7_days.lines[0]).toEqual(expected_lines[0]);
            });

            it("should show day nos on all 3 expected dates", function(){
                var expected_day_nos = [
                    {
                        day: 1,
                        type: "expected"
                    },
                    {
                        day: 3,
                        type: "expected"
                    },
                    {
                        day: 7,
                        type: "expected"
                    }
                ];

                pncService.preProcessFirst7Days(client, expected_visit_data, current_date);

                /// check day nos
                expect(client.visits.first_7_days.day_nos.length).toEqual(expected_day_nos.length);
                expect(client.visits.first_7_days.day_nos[0]).toEqual(expected_day_nos[0]);
                expect(client.visits.first_7_days.day_nos[1]).toEqual(expected_day_nos[1]);
                expect(client.visits.first_7_days.day_nos[2]).toEqual(expected_day_nos[2]);
            });
        });

        describe("2nd day with no services provided", function(){
            var client = {
                deliveryDate:"2013-05-13",
                services_provided:[]
            };

            var expected_visit_data;

            // set the current date to the day of delivery
            var current_date = new Date("2013-05-15");

            beforeEach(function(){
                // initialise first 7 days data
                client.visits = {first_7_days: {}};

                expected_visit_data = pncService.calculateExpectedVisitDates(client);
            });

            it("should create a colored circle for day 1", function () {
                var expected_circles = [
                    {
                        day: 1,
                        type: "expected",
                        colored: true
                    },
                    {
                        day: 3,
                        type: "expected",
                        colored: false
                    },
                    {
                        day: 7,
                        type: "expected",
                        colored: false
                    }
                ];

                pncService.preProcessFirst7Days(client, expected_visit_data, current_date);

                /// check circles
                expect(client.visits.first_7_days.circles.length).toEqual(expected_circles.length);
                expect(client.visits.first_7_days.circles[0]).toEqual(expected_circles[0]);
                expect(client.visits.first_7_days.circles[1]).toEqual(expected_circles[1]);
                expect(client.visits.first_7_days.circles[2]).toEqual(expected_circles[2]);
            });

            it("should create a missed status on day 1", function () {
                var expected_statuses = [
                    {
                        day: 1,
                        status: "missed"
                    }
                ];

                pncService.preProcessFirst7Days(client, expected_visit_data, current_date);

                /// check statuses
                expect(client.visits.first_7_days.statuses.length).toEqual(expected_statuses.length);
                expect(client.visits.first_7_days.statuses[0]).toEqual(expected_statuses[0]);
            });

            it("should set active color to red", function(){
                pncService.preProcessFirst7Days(client, expected_visit_data, current_date);

                /// check active color
                expect(client.visits.first_7_days.active_color).toEqual("red");
            });

            it("should create a colored tick on the current date and grey for the remainder", function () {
                var expected_ticks = [
                    {
                        day: 2,
                        type: "actual"
                    },
                    {
                        day: 4,
                        type: "expected"
                    },
                    {
                        day: 5,
                        type: "expected"
                    },
                    {
                        day: 6,
                        type: "expected"
                    }
                ];

                pncService.preProcessFirst7Days(client, expected_visit_data, current_date);

                /// check ticks
                expect(client.visits.first_7_days.ticks.length).toEqual(expected_ticks.length);
                expect(client.visits.first_7_days.ticks[0]).toEqual(expected_ticks[0]);
                expect(client.visits.first_7_days.ticks[1]).toEqual(expected_ticks[1]);
                expect(client.visits.first_7_days.ticks[2]).toEqual(expected_ticks[2]);
                expect(client.visits.first_7_days.ticks[3]).toEqual(expected_ticks[3]);
            });

            it("should create an actual line from 1 to 2 and an expected line from 2 to 7", function () {
                var expected_lines = [
                    {
                        start: 1,
                        end: 2,
                        type: "actual"
                    },
                    {
                        start: 2,
                        end: 7,
                        type: "expected"
                    }
                ];

                pncService.preProcessFirst7Days(client, expected_visit_data, current_date);

                /// check lines
                expect(client.visits.first_7_days.lines.length).toEqual(expected_lines.length);
                expect(client.visits.first_7_days.lines[0]).toEqual(expected_lines[0]);
                expect(client.visits.first_7_days.lines[1]).toEqual(expected_lines[1]);
            });

            it("should only show day nos on 3rd and 7th day", function(){
                var expected_day_nos = [
                    {
                        day: 3,
                        type: "expected"
                    },
                    {
                        day: 7,
                        type: "expected"
                    }
                ];

                pncService.preProcessFirst7Days(client, expected_visit_data, current_date);

                /// check day nos
                expect(client.visits.first_7_days.day_nos.length).toEqual(expected_day_nos.length);
                expect(client.visits.first_7_days.day_nos[0]).toEqual(expected_day_nos[0]);
                expect(client.visits.first_7_days.day_nos[1]).toEqual(expected_day_nos[1]);
            });
        });

        describe("3rd day with service provided on day 2", function(){
            var client = {
                deliveryDate:"2013-05-13",
                services_provided:[
                    {
                        name: "PNC",
                        date: "2013-05-15",
                        data: {}
                    }
                ]
            };

            var expected_visit_data;

            // set the current date to the day of delivery
            var current_date = new Date("2013-05-16");

            beforeEach(function(){
                // initialise first 7 days data
                client.visits = {first_7_days: {}};

                expected_visit_data = pncService.calculateExpectedVisitDates(client);
            });

            it("should create a circle of type actual on day 2", function () {
                var expected_circles = [
                    {
                        day: 1,
                        type: "expected",
                        colored: true
                    },
                    {
                        day: 3,
                        type: "expected",
                        colored: false
                    },
                    {
                        day: 7,
                        type: "expected",
                        colored: false
                    },
                    {
                        day: 2,
                        type: "actual"
                    }
                ];

                pncService.preProcessFirst7Days(client, expected_visit_data, current_date);

                /// check circles
                expect(client.visits.first_7_days.circles.length).toEqual(expected_circles.length);
                expect(client.visits.first_7_days.circles[0]).toEqual(expected_circles[0]);
                expect(client.visits.first_7_days.circles[1]).toEqual(expected_circles[1]);
                expect(client.visits.first_7_days.circles[2]).toEqual(expected_circles[2]);
                expect(client.visits.first_7_days.circles[3]).toEqual(expected_circles[3]);
            });

            it("should create a missed status on day 1", function () {
                var expected_statuses = [
                    {
                        day: 1,
                        status: "missed"
                    }
                ];

                pncService.preProcessFirst7Days(client, expected_visit_data, current_date);

                /// check statuses
                expect(client.visits.first_7_days.statuses.length).toEqual(expected_statuses.length);
                expect(client.visits.first_7_days.statuses[0]).toEqual(expected_statuses[0]);
            });

            it("should set active color to yellow", function(){
                pncService.preProcessFirst7Days(client, expected_visit_data, current_date);

                /// check active color
                expect(client.visits.first_7_days.active_color).toEqual("yellow");
            });

            it("should not create a tick on day 2 with grey ticks on the remainder", function () {
                var expected_ticks = [
                    {
                        day: 4,
                        type: "expected"
                    },
                    {
                        day: 5,
                        type: "expected"
                    },
                    {
                        day: 6,
                        type: "expected"
                    }
                ];

                pncService.preProcessFirst7Days(client, expected_visit_data, current_date);

                /// check ticks
                expect(client.visits.first_7_days.ticks.length).toEqual(expected_ticks.length);
                expect(client.visits.first_7_days.ticks[0]).toEqual(expected_ticks[0]);
                expect(client.visits.first_7_days.ticks[1]).toEqual(expected_ticks[1]);
                expect(client.visits.first_7_days.ticks[2]).toEqual(expected_ticks[2]);
            });

            it("should create an actual line from 1 to 3 and an expected line from 3 to 7", function () {
                var expected_lines = [
                    {
                        start: 1,
                        end: 3,
                        type: "actual"
                    },
                    {
                        start: 3,
                        end: 7,
                        type: "expected"
                    }
                ];

                pncService.preProcessFirst7Days(client, expected_visit_data, current_date);

                /// check lines
                expect(client.visits.first_7_days.lines.length).toEqual(expected_lines.length);
                expect(client.visits.first_7_days.lines[0]).toEqual(expected_lines[0]);
                expect(client.visits.first_7_days.lines[1]).toEqual(expected_lines[1]);
            });

            it("should show day nos on days 2, 3 and 7", function(){
                var expected_day_nos = [
                    /*{
                        day: 3,
                        type: "expected"
                    },*/
                    {
                        day: 7,
                        type: "expected"
                    },
                    {
                        day: 2,
                        type: "actual"
                    }
                ];

                pncService.preProcessFirst7Days(client, expected_visit_data, current_date);

                /// check day nos
                expect(client.visits.first_7_days.day_nos.length).toEqual(expected_day_nos.length);
                expect(client.visits.first_7_days.day_nos[0]).toEqual(expected_day_nos[0]);
                expect(client.visits.first_7_days.day_nos[1]).toEqual(expected_day_nos[1]);
//                expect(client.visits.first_7_days.day_nos[2]).toEqual(expected_day_nos[2]);
            });
        });

        describe("8th day with all services provided on time, and then some", function(){
            var client = {
                deliveryDate:"2013-05-13",
                services_provided:[
                    {
                        name: "PNC",
                        date: "2013-05-26",
                        data: {}
                    },
                    {
                        name: "PNC",
                        date: "2013-05-20",
                        data: {}
                    },
                    {
                        name: "PNC",
                        date: "2013-05-16",
                        data: {}
                    },
                    // spanner in the works, duplicate service date
                    {
                        name: "PNC",
                        date: "2013-05-16",
                        data: {}
                    },
                    {
                        name: "PNC",
                        date: "2013-05-15",
                        data: {}
                    },
                    {
                        name: "PNC",
                        date: "2013-05-14",
                        data: {}
                    }
                ]
            };

            var expected_visit_data;

            // set the current date to the day of delivery
            var current_date = new Date("2013-05-21");

            beforeEach(function(){
                // initialise first 7 days data
                client.visits = {first_7_days: {}};

                expected_visit_data = pncService.calculateExpectedVisitDates(client);
            });

            it("create circles of type actual on each expected day", function () {
                var expected_circles = [
                    {
                        day: 1,
                        type: "actual"
                    },
                    {
                        day: 2,
                        type: "actual"
                    },
                    {
                        day: 3,
                        type: "actual"
                    },
                    {
                        day: 7,
                        type: "actual"
                    }
                ];

                pncService.preProcessFirst7Days(client, expected_visit_data, current_date);

                /// check circles
                expect(client.visits.first_7_days.circles.length).toEqual(expected_circles.length);
                expect(client.visits.first_7_days.circles[0]).toEqual(expected_circles[0]);
                expect(client.visits.first_7_days.circles[1]).toEqual(expected_circles[1]);
                expect(client.visits.first_7_days.circles[2]).toEqual(expected_circles[2]);
            });

            it("should create done statuses for each expected day", function () {
                var expected_statuses = [
                    {
                        day: 1,
                        status: "done"
                    },
                    {
                        day: 3,
                        status: "done"
                    },
                    {
                        day: 7,
                        status: "done"
                    }
                ];

                pncService.preProcessFirst7Days(client, expected_visit_data, current_date);

                /// check statuses
                expect(client.visits.first_7_days.statuses.length).toEqual(expected_statuses.length);
                expect(client.visits.first_7_days.statuses[0]).toEqual(expected_statuses[0]);
                expect(client.visits.first_7_days.statuses[1]).toEqual(expected_statuses[1]);
                expect(client.visits.first_7_days.statuses[2]).toEqual(expected_statuses[2]);
            });

            it("should set active color to green", function(){
                pncService.preProcessFirst7Days(client, expected_visit_data, current_date);

                /// check active color
                expect(client.visits.first_7_days.active_color).toEqual("green");
            });

            it("should create ticks on days 4, 5 and 6", function () {
                var expected_ticks = [
                    {
                        day: 4,
                        type: "actual"
                    },
                    {
                        day: 5,
                        type: "actual"
                    },
                    {
                        day: 6,
                        type: "actual"
                    }
                ];

                pncService.preProcessFirst7Days(client, expected_visit_data, current_date);

                /// check ticks
                expect(client.visits.first_7_days.ticks.length).toEqual(expected_ticks.length);
                expect(client.visits.first_7_days.ticks[0]).toEqual(expected_ticks[0]);
                expect(client.visits.first_7_days.ticks[1]).toEqual(expected_ticks[1]);
                expect(client.visits.first_7_days.ticks[2]).toEqual(expected_ticks[2]);
                expect(client.visits.first_7_days.ticks[3]).toEqual(expected_ticks[3]);
            });

            it("should create an actual line from 1 to 7", function () {
                var expected_lines = [
                    {
                        start: 1,
                        end: 7,
                        type: "actual"
                    }
                ];

                pncService.preProcessFirst7Days(client, expected_visit_data, current_date);

                /// check lines
                expect(client.visits.first_7_days.lines.length).toEqual(expected_lines.length);
                expect(client.visits.first_7_days.lines[0]).toEqual(expected_lines[0]);
                expect(client.visits.first_7_days.lines[1]).toEqual(expected_lines[1]);
            });

            it("should show day nos 1, 2, 3 and 7 as actual", function(){
                var expected_day_nos = [
                    {
                        day:1,
                        type:"actual"
                    },
                    {
                        day:2,
                        type:"actual"
                    },
                    {
                        day:3,
                        type:"actual"
                    },
                    {
                        day:7,
                        type:"actual"
                    }
                ];

                pncService.preProcessFirst7Days(client, expected_visit_data, current_date);

                /// check day nos
                expect(client.visits.first_7_days.day_nos.length).toEqual(expected_day_nos.length);
                expect(client.visits.first_7_days.day_nos[0]).toEqual(expected_day_nos[0]);
                expect(client.visits.first_7_days.day_nos[1]).toEqual(expected_day_nos[1]);
                expect(client.visits.first_7_days.day_nos[2]).toEqual(expected_day_nos[2]);
            });
        });

        describe("8th day with no services provided", function(){
            var client = {
                deliveryDate: "2013-05-13",
                services_provided: []
            };

            var expected_visit_data;

            // set the current date to the day of delivery
            var current_date = new Date("2013-05-21");

            beforeEach(function(){
                // initialise first 7 days data
                client.visits = {first_7_days: {}};

                expected_visit_data = pncService.calculateExpectedVisitDates(client);
            });

            it("should create circles of type expected on each expected day", function () {
                var expected_circles = [
                    {
                        day: 1,
                        type: "expected",
                        colored: true
                    },
                    {
                        day: 3,
                        type: "expected",
                        colored: true
                    },
                    {
                        day: 7,
                        type: "expected",
                        colored: true
                    }
                ];

                pncService.preProcessFirst7Days(client, expected_visit_data, current_date);

                /// check circles
                expect(client.visits.first_7_days.circles.length).toEqual(expected_circles.length);
                expect(client.visits.first_7_days.circles[0]).toEqual(expected_circles[0]);
                expect(client.visits.first_7_days.circles[1]).toEqual(expected_circles[1]);
                expect(client.visits.first_7_days.circles[2]).toEqual(expected_circles[2]);
            });

            it("should create missed statuses for each expected day", function () {
                var expected_statuses = [
                    {
                        day: 1,
                        status: "missed"
                    },
                    {
                        day: 3,
                        status: "missed"
                    },
                    {
                        day: 7,
                        status: "missed"
                    }
                ];

                pncService.preProcessFirst7Days(client, expected_visit_data, current_date);

                /// check statuses
                expect(client.visits.first_7_days.statuses.length).toEqual(expected_statuses.length);
                expect(client.visits.first_7_days.statuses[0]).toEqual(expected_statuses[0]);
                expect(client.visits.first_7_days.statuses[1]).toEqual(expected_statuses[1]);
                expect(client.visits.first_7_days.statuses[2]).toEqual(expected_statuses[2]);
            });

            it("should set active color to red", function(){
                pncService.preProcessFirst7Days(client, expected_visit_data, current_date);

                /// check active color
                expect(client.visits.first_7_days.active_color).toEqual("red");
            });

            it("should create ticks on days 2,4,5,6", function () {
                var expected_ticks = [
                    {
                        day: 2,
                        type: "actual"
                    },
                    {
                        day: 4,
                        type: "actual"
                    },
                    {
                        day: 5,
                        type: "actual"
                    },
                    {
                        day: 6,
                        type: "actual"
                    }
                ];

                pncService.preProcessFirst7Days(client, expected_visit_data, current_date);

                /// check ticks
                expect(client.visits.first_7_days.ticks.length).toEqual(expected_ticks.length);
                expect(client.visits.first_7_days.ticks[0]).toEqual(expected_ticks[0]);
                expect(client.visits.first_7_days.ticks[1]).toEqual(expected_ticks[1]);
                expect(client.visits.first_7_days.ticks[2]).toEqual(expected_ticks[2]);
                expect(client.visits.first_7_days.ticks[3]).toEqual(expected_ticks[3]);
            });

            it("should create an actual line from 1 to 7", function () {
                var expected_lines = [
                    {
                        start: 1,
                        end: 7,
                        type: "actual"
                    }
                ];

                pncService.preProcessFirst7Days(client, expected_visit_data, current_date);

                /// check lines
                expect(client.visits.first_7_days.lines.length).toEqual(expected_lines.length);
                expect(client.visits.first_7_days.lines[0]).toEqual(expected_lines[0]);
            });

            it("should not show any day nos", function(){
                var expected_day_nos = [];

                pncService.preProcessFirst7Days(client, expected_visit_data, current_date);

                /// check day nos
                expect(client.visits.first_7_days.day_nos.length).toEqual(expected_day_nos.length);
            });
        });
    });
});


describe("Smart Helpers", function () {

    var smartHelper;

    beforeEach(module("smartRegistry.services"));
    beforeEach(inject(function (SmartHelper) {
        smartHelper = SmartHelper;
    }));

    describe("Date difference function", function(){
        it("should calculate the difference between an earlier date and a later date as positive", function(){
            expect(
                smartHelper.daysBetween(
                    new Date(Date.parse("2012-02-29")), new Date(Date.parse("2012-03-01")))).toEqual(1);
        });

        it("should calculate the difference between a later date and an earlier date as negative", function(){
            expect(
                smartHelper.daysBetween(
                    new Date(Date.parse("2012-03-01")), new Date(Date.parse("2012-02-29")))).toEqual(-1);
        });
    });

    describe("Zero Padding", function(){
       it("should pad a number with a zero if the number is less than 10", function(){
           var num = 6;

           var result = smartHelper.zeroPad(num);
           var expected = "06";

           expect(result).toEqual(expected);
       });

        it("should NOT pad a number with a zero if the number is greater than 9", function(){
            var num = 12;

            var result = smartHelper.zeroPad(num);
            var expected = "12";

            expect(result).toEqual(expected);
        });
    });

    describe("Childs age", function(){
        it("should return the age in days if less that 28", function () {
            var dob = new Date(Date.parse("2010-01-02"));
            var ref_date = new Date(dob);
            ref_date.setDate(ref_date.getDate() + (7 * 4 - 1));

            expect(smartHelper.childsAge(dob, ref_date)).toEqual("27d");
        });

        it("should return the age in weeks if between 4 weeks and 16 weeks", function () {
            var dob = new Date(Date.parse("2010-01-02"));
            var ref_date = new Date(dob);
            ref_date.setDate(ref_date.getDate() + (7 * 4));

            expect(smartHelper.childsAge(dob, ref_date)).toEqual("4w");

            ref_date = new Date(dob);
            ref_date.setDate(ref_date.getDate() + (7 * 17 - 1));

            expect(smartHelper.childsAge(dob, ref_date)).toEqual("16w");
        });

        it("should return the age in months if between 4 months and 23 months", function () {
            var dob = new Date(Date.parse("2010-01-02"));
            var ref_date = new Date(dob);
            var NO_OF_DAYS_IN_A_WEEK = 7;
            var NO_OF_WEEKS = 17;
            ref_date.setDate(ref_date.getDate() + (NO_OF_DAYS_IN_A_WEEK * NO_OF_WEEKS));

            expect(smartHelper.childsAge(dob, ref_date)).toEqual("3m");

            ref_date = new Date(Date.parse("2011-11-23"));

            expect(smartHelper.childsAge(dob, ref_date)).toEqual("23m");
        });

        it("should return the age in years if older than 23 months", function () {
            var dob = new Date(Date.parse("2010-01-02"));
            var ref_date = new Date(dob);
            ref_date.setDate(ref_date.getDate() + (365 * 2));

            expect(smartHelper.childsAge(dob, ref_date)).toEqual("2y");
        });
    });
});

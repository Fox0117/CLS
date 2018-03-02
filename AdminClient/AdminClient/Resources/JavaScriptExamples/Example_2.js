// parameters:
// {
//     "total_visits": 101,
//     "last_year_visits": 50,
//     "last_month_visits": 30,
//     "last_week_visits": 15,
//     "last_day_visits": 2
// }

function getMessage(parameters) {
    if (parameters.total_visits > 1000)
        return "Скидка 15%";

    if (parameters.total_visits > 100) {
        if (parameters.last_week_visits > 10)
            return "Скидка 10%";

        return "Скидка 7%";
    }

    if (parameters.total_visits > 5)
        return "Скидка 5%";

    return "Без скидки";
}
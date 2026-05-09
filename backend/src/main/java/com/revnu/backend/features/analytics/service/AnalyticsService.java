package com.revnu.backend.features.analytics.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.revnu.backend.features.analytics.dto.DailyAnalyticsResponse;
import com.revnu.backend.features.analytics.dto.HourlyExpensePoint;
import com.revnu.backend.features.analytics.dto.HourlySalePoint;
import com.revnu.backend.features.analytics.dto.ProfitTrendPoint;
import com.revnu.backend.features.analytics.dto.SalesTrendPoint;
import com.revnu.backend.features.analytics.dto.TagBreakdownItem;
import com.revnu.backend.features.analytics.dto.TagSlice;
import com.revnu.backend.features.analytics.dto.TrendAnalyticsResponse;
import com.revnu.backend.features.analytics.repository.AnalyticsRepository;
import com.revnu.backend.features.auth.model.User;
import com.revnu.backend.features.auth.repository.UserRepository;
import com.revnu.backend.features.expenses.model.ExpenseStatus;
import com.revnu.backend.features.reporting.model.DailySummary;
import com.revnu.backend.features.reporting.repository.DailySummaryRepository;
import com.revnu.backend.features.restaurants.model.Restaurant;
import com.revnu.backend.features.restaurants.repository.RestaurantRepository;
import com.revnu.backend.features.sales.model.SaleStatus;
import com.revnu.backend.features.staff.model.SalaryStatus;
import com.revnu.backend.shared.util.DateFormatUtil;

@Service
public class AnalyticsService {

    private final AnalyticsRepository analyticsRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final DailySummaryRepository summaryRepository;

    private static final List<String> CATEGORY_COLORS = List.of(
            "#5a7cff", "#ffb800", "#ff5a8d", "#4ade80", "#a78bfa", "#fb923c"
    );

    public AnalyticsService(
            AnalyticsRepository analyticsRepository,
            RestaurantRepository restaurantRepository,
            UserRepository userRepository,
            DailySummaryRepository summaryRepository
    ) {
        this.analyticsRepository = analyticsRepository;
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
        this.summaryRepository = summaryRepository;
    }

    //  Helpers 
    private Restaurant getRestaurantByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));
        return restaurantRepository.findByOwner(user)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found for user: " + email));
    }

    //  Daily Analytics 
    public DailyAnalyticsResponse getDailyAnalytics(String email, LocalDate targetDate) {
        Restaurant restaurant = getRestaurantByEmail(email);
        UUID restaurantId = restaurant.getId();
        LocalDate yesterday = targetDate.minusDays(1);

        DailySummary todaySummary = summaryRepository
                .findByRestaurantAndReportDate(restaurant, targetDate)
                .orElse(null);
        boolean isClosed = todaySummary != null;

        SaleStatus todaySaleStatus = isClosed ? SaleStatus.CLOSED : SaleStatus.OPEN;
        ExpenseStatus todayExpenseStatus = isClosed ? ExpenseStatus.CLOSED : ExpenseStatus.OPEN;
        SalaryStatus todaySalaryStatus = isClosed ? SalaryStatus.CLOSED : SalaryStatus.OPEN;

        DailySummary yesterdaySummary = summaryRepository
                .findByRestaurantAndReportDate(restaurant, yesterday)
                .orElse(null);
        SaleStatus yesterdaySaleStatus = yesterdaySummary != null ? SaleStatus.CLOSED : SaleStatus.OPEN;
        ExpenseStatus yesterdayExpenseStatus = yesterdaySummary != null ? ExpenseStatus.CLOSED : ExpenseStatus.OPEN;
        SalaryStatus yesterdaySalaryStatus = yesterdaySummary != null ? SalaryStatus.CLOSED : SalaryStatus.OPEN;

        // KPI Totals
        BigDecimal totalSales = todaySummary != null
                ? todaySummary.getTotalSales()
                : analyticsRepository.sumSalesByRestaurantAndDate(restaurantId, targetDate, todaySaleStatus);
        BigDecimal totalExpenses = todaySummary != null
                ? todaySummary.getTotalExpenses()
                : analyticsRepository.sumExpensesByRestaurantAndDate(restaurantId, targetDate, todayExpenseStatus);
        BigDecimal totalSalaries = todaySummary != null
                ? todaySummary.getTotalSalaries()
                : analyticsRepository.sumSalariesByRestaurantAndDate(restaurantId, targetDate, todaySalaryStatus);
        BigDecimal netProfit = todaySummary != null
                ? todaySummary.getNetProfit()
                : totalSales.subtract(totalExpenses).subtract(totalSalaries);

        // Record Counts
        long saleRecordsCount = analyticsRepository.countSalesByRestaurantAndDate(
                restaurantId, targetDate, todaySaleStatus);
        long expenseRecordsCount = analyticsRepository.countExpensesByRestaurantAndDate(
                restaurantId, targetDate, todayExpenseStatus);

        // Yesterday KPIs
        BigDecimal yesterdaySales = yesterdaySummary != null
                ? yesterdaySummary.getTotalSales()
                : analyticsRepository.sumSalesByRestaurantAndDate(restaurantId, yesterday, yesterdaySaleStatus);
        BigDecimal yesterdayExpenses = yesterdaySummary != null
                ? yesterdaySummary.getTotalExpenses()
                : analyticsRepository.sumExpensesByRestaurantAndDate(restaurantId, yesterday, yesterdayExpenseStatus);
        BigDecimal yesterdaySalaries = yesterdaySummary != null
                ? yesterdaySummary.getTotalSalaries()
                : analyticsRepository.sumSalariesByRestaurantAndDate(restaurantId, yesterday, yesterdaySalaryStatus);
        BigDecimal yesterdayProfit = yesterdaySummary != null
                ? yesterdaySummary.getNetProfit()
                : yesterdaySales.subtract(yesterdayExpenses.add(yesterdaySalaries));

        // Sales Trend & Expense Trend: hourly today vs yesterday
        List<HourlySalePoint> todaySalesPts = analyticsRepository.hourlySalesByRestaurantAndDate(restaurantId, targetDate, todaySaleStatus);
        List<HourlySalePoint> yesterdaySalesPts = analyticsRepository.hourlySalesByRestaurantAndDate(restaurantId, yesterday, yesterdaySaleStatus);

        List<HourlyExpensePoint> todayExpPts = analyticsRepository.hourlyExpensesByRestaurantAndDate(restaurantId, targetDate, todayExpenseStatus);
        List<HourlyExpensePoint> yesterdayExpPts = analyticsRepository.hourlyExpensesByRestaurantAndDate(restaurantId, yesterday, yesterdayExpenseStatus);

        Map<Integer, BigDecimal> todaySalesMap = todaySalesPts.stream().collect(Collectors.toMap(HourlySalePoint::hour, HourlySalePoint::total));
        Map<Integer, BigDecimal> yesterdaySalesMap = yesterdaySalesPts.stream().collect(Collectors.toMap(HourlySalePoint::hour, HourlySalePoint::total));

        Map<Integer, BigDecimal> todayExpMap = todayExpPts.stream().collect(Collectors.toMap(HourlyExpensePoint::hour, HourlyExpensePoint::total));
        Map<Integer, BigDecimal> yesterdayExpMap = yesterdayExpPts.stream().collect(Collectors.toMap(HourlyExpensePoint::hour, HourlyExpensePoint::total));

        Set<Integer> allHours = new TreeSet<>();
        todaySalesPts.forEach(p -> allHours.add(p.hour()));
        yesterdaySalesPts.forEach(p -> allHours.add(p.hour()));
        todayExpPts.forEach(p -> allHours.add(p.hour()));
        yesterdayExpPts.forEach(p -> allHours.add(p.hour()));

        if (allHours.isEmpty()) {
            for (int h = 8; h <= 22; h += 2) {
                allHours.add(h);
            }
        }

        List<SalesTrendPoint> salesTrend = allHours.stream()
                .map(hour -> new SalesTrendPoint(
                DateFormatUtil.formatHour(hour),
                todaySalesMap.getOrDefault(hour, BigDecimal.ZERO),
                todayExpMap.getOrDefault(hour, BigDecimal.ZERO),
                yesterdaySalesMap.getOrDefault(hour, BigDecimal.ZERO),
                yesterdayExpMap.getOrDefault(hour, BigDecimal.ZERO)
        ))
                .collect(Collectors.toList());

        // Tag Breakdown (pie chart)
        List<TagBreakdownItem> rawCategories = analyticsRepository.salesByTagAndDate(
                restaurantId, targetDate, todaySaleStatus);
        List<TagSlice> categories = new ArrayList<>();
        for (int i = 0; i < rawCategories.size(); i++) {
            TagBreakdownItem item = rawCategories.get(i);
            categories.add(new TagSlice(item.name(), item.total(), CATEGORY_COLORS.get(i % CATEGORY_COLORS.size())));
        }

        String yesterdayLabel = DateFormatUtil.formatDate(yesterday, "dd MMM");

        return new DailyAnalyticsResponse(
                totalSales, totalExpenses, totalSalaries, netProfit,
                yesterdaySales, yesterdayExpenses, yesterdayProfit,
                saleRecordsCount, expenseRecordsCount,
                salesTrend, categories,
                yesterdayLabel, targetDate.toString(), isClosed
        );
    }

    //  Profit Trend 
    public TrendAnalyticsResponse getTrends(String email, String period) {
        Restaurant restaurant = getRestaurantByEmail(email);

        List<DailySummary> summaries = analyticsRepository.findRecentSummaries(
                restaurant.getId(), LocalDate.now(), PageRequest.of(0, 7));

        Collections.reverse(summaries);

        List<ProfitTrendPoint> points = summaries.stream()
                .map(ds -> new ProfitTrendPoint(
                DateFormatUtil.formatDate(ds.getReportDate(), "dd MMM"),
                ds.getTotalSales(),
                ds.getTotalExpenses(),
                ds.getNetProfit()
        ))
                .collect(Collectors.toList());

        return new TrendAnalyticsResponse(period, points);
    }
}

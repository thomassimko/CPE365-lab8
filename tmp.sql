SELECT absYears.Year, absYears.Place, absYears.ticker as Absolute, relYears.ticker as Relative
FROM (SELECT abs1.year, abs1.ticker, abs1.Absolute, count(*) as Place
	FROM (SELECT tDays.year, tDays.ticker, ap2.Close-ap1.Open as Absolute
		FROM (((SELECT YEAR(day) as year, ticker, min(day) as YearOpen, max(day) as YearClose
			FROM AdjustedPrices p
			GROUP BY year, ticker) tDays JOIN AdjustedPrices ap1 
				on (tDays.YearOpen=ap1.day and tDays.ticker=ap1.ticker))
			JOIN AdjustedPrices ap2 ON (tDays.ticker=ap2.ticker and tDays.YearClose=ap2.day))
		GROUP BY year, ticker) abs1,
		(SELECT tDays.year, tDays.ticker, ap2.Close-ap1.Open as Absolute
		FROM (((SELECT YEAR(day) as year, ticker, min(day) as YearOpen, max(day) as YearClose
			FROM AdjustedPrices p
			GROUP BY year, ticker) tDays JOIN AdjustedPrices ap1 
				on (tDays.YearOpen=ap1.day and tDays.ticker=ap1.ticker))
			JOIN AdjustedPrices ap2 ON (tDays.ticker=ap2.ticker and tDays.YearClose=ap2.day))
		GROUP BY year, ticker) abs2
	WHERE abs1.year=abs2.year and abs1.Absolute<=abs2.Absolute
	GROUP BY abs1.year, abs1.ticker
	HAVING Place <= 5
	ORDER BY abs1.year, abs1.Absolute, Place DESC
	) absYears
JOIN
	(SELECT rel1.year, rel1.ticker, rel1.Relative, count(*) as Place
	FROM (SELECT tDays.year, tDays.ticker, 100*(ap2.Close/ap1.Open) as Relative
		FROM (((SELECT YEAR(day) as year, ticker, min(day) as YearOpen, max(day) as YearClose
			FROM AdjustedPrices p
			GROUP BY year, ticker) tDays JOIN AdjustedPrices ap1 
				on (tDays.YearOpen=ap1.day and tDays.ticker=ap1.ticker))
			JOIN AdjustedPrices ap2 ON (tDays.ticker=ap2.ticker and tDays.YearClose=ap2.day))
		GROUP BY year, ticker) rel1,
		(SELECT tDays.year, tDays.ticker, 100*(ap2.Close/ap1.Open) as Relative
		FROM (((SELECT YEAR(day) as year, ticker, min(day) as YearOpen, max(day) as YearClose
			FROM AdjustedPrices p
			GROUP BY year, ticker) tDays JOIN AdjustedPrices ap1 
				on (tDays.YearOpen=ap1.day and tDays.ticker=ap1.ticker))
			JOIN AdjustedPrices ap2 ON (tDays.ticker=ap2.ticker and tDays.YearClose=ap2.day))
		GROUP BY year, ticker) rel2
	WHERE rel1.year=rel2.year and rel1.Relative<=rel2.Relative
	GROUP BY rel1.year, rel1.ticker
	HAVING Place <= 5
	ORDER BY rel1.year, Place DESC
	)relYears on (absYears.year=relYears.Year and absYears.Place=relYears.Place)
ORDER BY year, Place;

SELECT tDays.year, tDays.ticker, 100*(ap2.Close/ap1.Open) as Relative
FROM (((SELECT YEAR(day) as year, ticker, min(day) as YearOpen, max(day) as YearClose
	FROM AdjustedPrices p
	GROUP BY year, ticker) tDays JOIN AdjustedPrices ap1 
		on (tDays.YearOpen=ap1.day and tDays.ticker=ap1.ticker))
	JOIN AdjustedPrices ap2 ON (tDays.ticker=ap2.ticker and tDays.YearClose=ap2.day))
WHERE year=2011
GROUP BY year, ticker
ORDER BY Relative DESC
LIMIT 5;

SELECT tDays.year, tDays.ticker, ap2.Close-ap1.Open as Absolute
FROM (((SELECT YEAR(day) as year, ticker, min(day) as YearOpen, max(day) as YearClose
	FROM AdjustedPrices p
	GROUP BY year, ticker) tDays JOIN AdjustedPrices ap1 
		on (tDays.YearOpen=ap1.day and tDays.ticker=ap1.ticker))
	JOIN AdjustedPrices ap2 ON (tDays.ticker=ap2.ticker and tDays.YearClose=ap2.day))
WHERE year=2011
GROUP BY year, ticker
ORDER BY Absolute DESC
LIMIT 5;
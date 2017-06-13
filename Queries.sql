-- General Q1

SELECT Start, End, PriceInc, PriceDec
FROM

	(SELECT COUNT(DISTINCT Ticker) as Start
	FROM Prices p
	WHERE p.Day <= ALL (SELECT Day from Prices where YEAR(Day) = 2016) and
      YEAR(Day) = 2016) q1,

	(SELECT COUNT(DISTINCT Ticker) as End
	FROM Prices p
	WHERE p.Day >= ALL (SELECT Day from Prices where YEAR(Day) = 2016) and
      YEAR(Day) = 2016) q2,

	(SELECT COUNT(DISTINCT p1.Ticker) as PriceInc
	FROM Prices p1, Prices p2
	WHERE p1.ticker = p2.ticker and p1.Day >= ALL (SELECT Day from Prices where YEAR(Day) = 2015) and YEAR(p1.Day) = 2015 and p2.Day >= ALL (SELECT Day from Prices where YEAR(Day) = 2016) and YEAR(p2.Day) = 2016 and p1.Close < p2.Close) q3,

	(SELECT COUNT(DISTINCT p1.Ticker) as PriceDec
	FROM Prices p1, Prices p2
	WHERE p1.ticker = p2.ticker and p1.Day >= ALL (SELECT Day from Prices where YEAR(Day) = 2015) and YEAR(p1.Day) = 2015 and p2.Day >= ALL (SELECT Day from Prices where YEAR(Day) = 2016) and YEAR(p2.Day) = 2016 and p1.Close > p2.Close) q4

;


SELECT DATEFORMAT(p.day, '%y' ) as Year, p.ticker, SUM(volume), AVG(close), AVG(volume)
FROM Prices p
WHERE ticker='KLAC'
GROUP BY year, ticker;

-- General Q2

SELECT Ticker
FROM Prices p
WHERE YEAR(p.day) = 2016
GROUP BY Ticker
ORDER BY SUM(p.volume) DESC
LIMIT 10;


-- General Q3

SELECT absYears.Year, absYears.Place, absYears.ticker as Absolute, 
         relYears.ticker as Relative
FROM (SELECT abs1.year, abs1.ticker, abs1.Absolute, count(*) as Place
   FROM (SELECT tDays.year, tDays.ticker, ap2.Close-ap1.Open as Absolute
      FROM (((SELECT YEAR(day) as year, ticker, min(day) as YearOpen, 
            max(day) as YearClose
         FROM AdjustedPrices p
         GROUP BY year, ticker) tDays JOIN AdjustedPrices ap1 
            on (tDays.YearOpen=ap1.day and tDays.ticker=ap1.ticker))
         JOIN AdjustedPrices ap2 ON (tDays.ticker=ap2.ticker and 
               tDays.YearClose=ap2.day))
      GROUP BY year, ticker) abs1,
      (SELECT tDays.year, tDays.ticker, ap2.Close-ap1.Open as Absolute
      FROM (((SELECT YEAR(day) as year, ticker, min(day) as YearOpen, 
               max(day) as YearClose
         FROM AdjustedPrices p
         GROUP BY year, ticker) tDays JOIN AdjustedPrices ap1 
            on (tDays.YearOpen=ap1.day and tDays.ticker=ap1.ticker))
         JOIN AdjustedPrices ap2 ON (tDays.ticker=ap2.ticker and 
               tDays.YearClose=ap2.day))
      GROUP BY year, ticker) abs2
   WHERE abs1.year=abs2.year and abs1.Absolute<=abs2.Absolute
   GROUP BY abs1.year, abs1.ticker
   HAVING Place <= 5
   ORDER BY abs1.year, abs1.Absolute, Place DESC
   ) absYears
JOIN
   (SELECT rel1.year, rel1.ticker, rel1.Relative, count(*) as Place
   FROM (SELECT tDays.year, tDays.ticker, 100*(ap2.Close/ap1.Open) as Relative
      FROM (((SELECT YEAR(day) as year, ticker, min(day) as YearOpen, max(day) 
                  as YearClose
         FROM AdjustedPrices p
         GROUP BY year, ticker) tDays JOIN AdjustedPrices ap1 
            on (tDays.YearOpen=ap1.day and tDays.ticker=ap1.ticker))
         JOIN AdjustedPrices ap2 ON (tDays.ticker=ap2.ticker and 
                  tDays.YearClose=ap2.day))
      GROUP BY year, ticker) rel1,
      (SELECT tDays.year, tDays.ticker, 100*(ap2.Close/ap1.Open) as Relative
      FROM (((SELECT YEAR(day) as year, ticker, min(day) as YearOpen, max(day) 
               as YearClose
         FROM AdjustedPrices p
         GROUP BY year, ticker) tDays JOIN AdjustedPrices ap1 
            on (tDays.YearOpen=ap1.day and tDays.ticker=ap1.ticker))
         JOIN AdjustedPrices ap2 ON (tDays.ticker=ap2.ticker and 
                  tDays.YearClose=ap2.day))
      GROUP BY year, ticker) rel2
   WHERE rel1.year=rel2.year and rel1.Relative<=rel2.Relative
   GROUP BY rel1.year, rel1.ticker
   HAVING Place <= 5
   ORDER BY rel1.year, Place DESC
   )relYears on (absYears.year=relYears.Year and absYears.Place=relYears.Place)
ORDER BY year, Place;

-- Individual 1
SELECT s.ticker, s.name, MIN(p.DAY) as FirstDay, MAX(p.Day) as LastDay
FROM Securities s join Prices p on s.ticker=p.ticker
WHERE s.ticker='KLAC';

-- Individual 2
SELECT tDays.year, tDays.ticker, s.name, p2.Close-p1.Open as PriceChange, 
      tDays.TotalVolume, tDays.AvgClose, tDays.AvgVol
FROM (Securities s join AdjustedPrices p1 on s.ticker=p1.ticker) join 
      AdjustedPrices p2 on s.ticker=p2.ticker,
      (SELECT s.ticker, YEAR(p.day) as year, MIN(p.DAY) as FirstDay, MAX(p.Day)
                as LastDay, SUM(p.Volume) as TotalVolume, AVG(p.Close) as 
                  AvgClose, AVG(p.Volume) as AvgVol
       FROM Securities s join AdjustedPrices p on s.ticker=p.ticker
       WHERE s.ticker='KLAC'
       GROUP BY year) tDays
WHERE s.ticker='KLAC' and p1.day=tDays.FirstDay and p2.day=tDays.LastDay;

-- Individual 3

SELECT DATE_FORMAT(p.day, '%M') as Month, p.ticker, AVG(p.close) as AvgClose, MAX(p.high) 
         as MonthlyHigh, MIN(p.low) as MonthlyLow, AVG(p.volume) as AvgVol
FROM AdjustedPrices p
WHERE p.ticker='KLAC' and YEAR(p.day)>= ALL(SELECT DISTINCT YEAR(p.day)
                                             FROM AdjustedPrices p
                                             WHERE p.ticker='KLAC')
GROUP BY Month
ORDER BY Month(p.Day);

-- Individual 4
-- Determine the month of best performance for your stock for each of
-- the years. Explain the criteria used to determine the month of best
-- performance in your HTML text, and provide the results2

-- Monthly Sector Performance for specified ticker
SELECT stats.*, AVG(oSect.Price) as AvgOpen, AVG(cSect.Price) as AvgClose,
      AVG(cSect.Price-oSect.Price) as AvgAbsChange,
      100*AVG(cSect.Price/oSect.Price) as AvgRelChange
FROM (((SELECT s.Sector, Year(ap.day) as year, Month(ap.day) as month, MIN(ap.day) as FirstDay,MAX(ap.day) as 
         LastDay, AVG(ap.Volume) as AvgVol, AVG(ap.low) as MonthlyLow, 
         AVG(ap.high) as MonthlyHigh
   FROM (AdjustedPrices ap JOIN Securities s on s.ticker=ap.ticker)
   WHERE s.Sector IN (SELECT s.Sector
                      FROM Securities s
                      WHERE s.ticker='KLAC')
   GROUP BY s.Sector,year, month) stats 
   LEFT JOIN (SELECT ap.ticker, s.sector, ap.day, ap.Open as Price
              FROM AdjustedPrices ap JOIN Securities s on s.ticker=ap.ticker
              WHERE s.Sector IN (SELECT s.Sector
                                 FROM Securities s
                                 WHERE s.ticker='KLAC')) oSect
   ON oSect.day=stats.FirstDay)
   LEFT JOIN (SELECT ap.ticker, s.sector, ap.day, ap.close as Price
              FROM AdjustedPrices ap JOIN Securities s on s.ticker=ap.ticker
              WHERE s.Sector IN (SELECT s.Sector
                                 FROM Securities s
                                 WHERE s.ticker='KLAC')) cSect
   ON cSect.day=stats.LastDay)
GROUP BY stats.year, stats.month;

-- Monthly 

SELECT stats.*, AVG(open.Price) as AvgOpen, AVG(close.Price) as AvgClose,
      AVG(close.Price-open.Price) as AvgAbsChange,
      100*AVG(close.Price/open.Price) as AvgRelChange
FROM (((SELECT ap.ticker, Year(ap.day) as year, Month(ap.day) as month, MIN(ap.day) as FirstDay,MAX(ap.day) as 
         LastDay, AVG(ap.Volume) as AvgVol, AVG(ap.low) as MonthlyLow, 
         AVG(ap.high) as MonthlyHigh
   FROM AdjustedPrices ap
   WHERE ap.ticker='KLAC'
   GROUP BY year, month) stats 
   LEFT JOIN (SELECT ap.ticker, ap.day, ap.Open as Price
              FROM AdjustedPrices ap
              WHERE ap.ticker='KLAC'
              ) open
   ON open.day=stats.FirstDay)
   LEFT JOIN (SELECT ap.ticker, ap.day, ap.close as Price
              FROM AdjustedPrices ap 
              WHERE ap.ticker='KLAC') close
   ON close.day=stats.LastDay)
GROUP BY stats.year, stats.month;


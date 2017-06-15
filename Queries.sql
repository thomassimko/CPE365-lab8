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

-- General Q2

SELECT t1.ticker, t1.volumeTraded
FROM
   (SELECT Ticker, SUM(p.volume) as volumeTraded
   FROM Prices p
   WHERE YEAR(p.day) = 2016
   GROUP BY Ticker
   ORDER BY s DESC) t1,

   (SELECT Ticker, SUM(p.volume) as volumeTraded
   FROM Prices p
   WHERE YEAR(p.day) = 2016
   GROUP BY Ticker) t2
WHERE t1.volumeTraded <= t2.volumeTraded
GROUP BY t1.ticker, t1.volumeTraded
HAVING count(*) <= 10
ORDER BY volumeTraded DESC;


-- General Q3

SELECT absYears.Year, absYears.Place, absYears.AbsoluteTicker,
         relYears.RelativeTicker
FROM (SELECT abs1.year, abs1.ticker as AbsoluteTicker, abs1.Absolute, count(*) as Place
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
   (SELECT rel1.year, rel1.ticker as RelativeTicker, rel1.Relative, count(*) as Place
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



-- General 4

SELECT r1.ticker, r1.relativeGrowth
FROM
   (SELECT t1.ticker, t2.close / t1.open as relativeGrowth
   FROM
      (SELECT p.ticker, p.day, p.open
      FROM
         Prices p,
         (SELECT ticker, min(day) as start, max(day) as end
         FROM Prices
         WHERE YEAR(day) = 2016
         GROUP BY ticker) days
      WHERE p.ticker = days.ticker and p.day = days.start) t1,

      (SELECT p.ticker, p.day, p.close
      FROM
         Prices p,
         (SELECT ticker, min(day) as start, max(day) as end
         FROM Prices
         WHERE YEAR(day) = 2016
         GROUP BY ticker) days
      WHERE p.ticker = days.ticker and p.day = days.end) t2
   WHERE t1.ticker = t2.ticker
   ORDER BY relativeGrowth DESC) r1,

   (SELECT t1.ticker, t2.close / t1.open as relativeGrowth
   FROM
      (SELECT p.ticker, p.day, p.open
      FROM
         Prices p,
         (SELECT ticker, min(day) as start, max(day) as end
         FROM Prices
         WHERE YEAR(day) = 2016
         GROUP BY ticker) days
      WHERE p.ticker = days.ticker and p.day = days.start) t1,

      (SELECT p.ticker, p.day, p.close
      FROM
         Prices p,
         (SELECT ticker, min(day) as start, max(day) as end
         FROM Prices
         WHERE YEAR(day) = 2016
         GROUP BY ticker) days
      WHERE p.ticker = days.ticker and p.day = days.end) t2
   WHERE t1.ticker = t2.ticker
   ORDER BY relativeGrowth DESC) r2
WHERE r1.relativeGrowth <= r2.relativeGrowth
GROUP BY r1.ticker, r1.relativeGrowth
HAVING count(*) <= 10
ORDER BY r1.relativeGrowth DESC;


-- General 5

SELECT Sector, Difference, Ratio, totalratio, totaldiff
FROM
   (SELECT s.Sector, AVG(z.diff) as Difference, AVG(z.ratio) as Ratio
   FROM
      Securities s,
      (SELECT t.ticker, p2.close / p1.open as ratio, p2.close - p1.open as diff
      FROM AdjustedPrices p1, AdjustedPrices p2,
         (SELECT ticker, min(day) as start, max(day) as end
         FROM AdjustedPrices p
         WHERE YEAR(day) = 2016
         GROUP BY ticker) t
      WHERE t.ticker = p1.ticker and t.ticker = p2.ticker and p1.day = t.start and
         p2.day = t.end) z
   WHERE s.ticker = z.ticker and s.sector != 'Telecommunications Services'
   GROUP BY s.Sector) s,

   (SELECT AVG(p2.close / p1.open) as totalratio, AVG(p2.close - p1.open) as totaldiff
   FROM AdjustedPrices p1, AdjustedPrices p2,
      (SELECT ticker, min(day) as start, max(day) as end
      FROM AdjustedPrices p
      WHERE YEAR(day) = 2016
      GROUP BY ticker) t
   WHERE t.ticker = p1.ticker and t.ticker = p2.ticker and p1.day = t.start and
      p2.day = t.end) t
;






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

-- Individual 5
-- Determine the month of best performance for your stock for each of
-- the years. Explain the criteria used to determine the month of best
-- performance in your HTML text, and provide the results2

-- Monthly Sector Performance for specified ticker up to the date specified
SELECT stats.year, stats.month, 100*AVG(cSect.Price/oSect.Price) as AvgRelChange
FROM (((SELECT s.Sector, Year(ap.day) as year, Month(ap.day) as month, MIN(ap.day) as FirstDay,MAX(ap.day) as 
         LastDay
   FROM (AdjustedPrices ap JOIN Securities s on s.ticker=ap.ticker)
   WHERE (s.Sector, s.Industry) = (SELECT s.Sector, s.Industry
                      FROM Securities s
                      WHERE s.ticker='KLAC') and ap.day<'2017-06-13'
   GROUP BY s.Sector,year, month) stats 
   LEFT JOIN (SELECT ap.ticker, s.sector, ap.day, ap.Open as Price
              FROM AdjustedPrices ap JOIN Securities s on s.ticker=ap.ticker
              WHERE (s.Sector, s.Industry) = (SELECT s.Sector, s.Industry
                                 FROM Securities s
                                 WHERE s.ticker='KLAC') and ap.day<'2017-06-13') oSect
   ON oSect.day=stats.FirstDay)
   LEFT JOIN (SELECT ap.ticker, s.sector, ap.day, ap.close as Price
              FROM AdjustedPrices ap JOIN Securities s on s.ticker=ap.ticker
              WHERE (s.Sector, s.Industry) = (SELECT s.Sector, s.Industry
                                               FROM Securities s
                                               WHERE s.ticker='KLAC') and ap.day<'2017-06-13') cSect
   ON cSect.day=stats.LastDay)
GROUP BY stats.year, stats.month;

-- Monthly Perfomance of specified ticker matching schema of above result
-- comparisons for determining best performance will be done in Java as they 
-- will be more efficient.

SELECT stats.year, stats.month, 100*AVG(close.Price/open.Price) as AvgRelChange
FROM (((SELECT ap.ticker, Year(ap.day) as year, Month(ap.day) as month, MIN(ap.day) as FirstDay,MAX(ap.day) as 
         LastDay
   FROM AdjustedPrices ap
   WHERE ap.ticker='KLAC' and ap.day<'2017-06-13'
   GROUP BY year, month) stats 
   LEFT JOIN (SELECT ap.ticker, ap.day, ap.Open as Price
              FROM AdjustedPrices ap
              WHERE ap.ticker='KLAC'  and ap.day<'2017-06-13'
              ) open
   ON open.day=stats.FirstDay)
   LEFT JOIN (SELECT ap.ticker, ap.day, ap.close as Price
              FROM AdjustedPrices ap 
              WHERE ap.ticker='KLAC' and ap.day<'2017-06-13') close
   ON close.day=stats.LastDay)
GROUP BY stats.year, stats.month;

SELECT sector.year, sector.month, stock.AvgRelChange as StockChange, sector.AvgRelChange as SectorChange,
      stock.AvgRelChange-sector.AvgRelChange as Difference
FROM (SELECT stats.year, stats.month, 100-100*AVG(close.Price/open.Price) as AvgRelChange
      FROM (((SELECT ap.ticker, Year(ap.day) as year, Month(ap.day) as month, MIN(ap.day) as FirstDay,MAX(ap.day) as 
               LastDay
         FROM AdjustedPrices ap
         WHERE ap.ticker='KLAC' and ap.day<'2017-06-13'
         GROUP BY year, month) stats 
         LEFT JOIN (SELECT ap.ticker, ap.day, ap.Open as Price
                    FROM AdjustedPrices ap
                    WHERE ap.ticker='KLAC'  and ap.day<'2017-06-13'
                    ) open
         ON open.day=stats.FirstDay)
         LEFT JOIN (SELECT ap.ticker, ap.day, ap.close as Price
                    FROM AdjustedPrices ap 
                    WHERE ap.ticker='KLAC' and ap.day<'2017-06-13') close
         ON close.day=stats.LastDay)
      GROUP BY stats.year, stats.month) stock JOIN
      (SELECT stats.year, stats.month, 100-100*AVG(cSect.Price/oSect.Price) as AvgRelChange
      FROM (((SELECT s.Sector, Year(ap.day) as year, Month(ap.day) as month, MIN(ap.day) as FirstDay,MAX(ap.day) as 
               LastDay
         FROM (AdjustedPrices ap JOIN Securities s on s.ticker=ap.ticker)
         WHERE (s.Sector, s.Industry) = (SELECT s.Sector, s.Industry
                            FROM Securities s
                            WHERE s.ticker='KLAC') and ap.day<'2017-06-13'
         GROUP BY s.Sector,year, month) stats 
         LEFT JOIN (SELECT ap.ticker, s.sector, ap.day, ap.Open as Price
                    FROM AdjustedPrices ap JOIN Securities s on s.ticker=ap.ticker
                    WHERE (s.Sector, s.Industry) = (SELECT s.Sector, s.Industry
                                       FROM Securities s
                                       WHERE s.ticker='KLAC') and ap.day<'2017-06-13') oSect
         ON oSect.day=stats.FirstDay)
         LEFT JOIN (SELECT ap.ticker, s.sector, ap.day, ap.close as Price
                    FROM AdjustedPrices ap JOIN Securities s on s.ticker=ap.ticker
                    WHERE (s.Sector, s.Industry) = (SELECT s.Sector, s.Industry
                                                     FROM Securities s
                                                     WHERE s.ticker='KLAC') and ap.day<'2017-06-13') cSect
         ON cSect.day=stats.LastDay)
      GROUP BY stats.year, stats.month) sector on (sector.year=stock.year and sector.month=stock.month);





-----------------------------------------------------------------------------------------------------------------
-- Individual 4
SELECT counts.*
FROM (SELECT stock.year, stock.month, stock.AvgRelChange-industry.AvgRelChange as Difference
      FROM (SELECT stats.year, stats.month, 100*AVG(close.Price/open.Price) as AvgRelChange
            FROM (((SELECT ap.ticker, Year(ap.day) as year, Month(ap.day) as month, MIN(ap.day) as FirstDay,MAX(ap.day) as 
                     LastDay
               FROM AdjustedPrices ap
               WHERE ap.ticker='KLAC' and ap.day<'2017-06-13'
               GROUP BY year, month) stats 
               LEFT JOIN (SELECT ap.ticker, ap.day, ap.Open as Price
                          FROM AdjustedPrices ap
                          WHERE ap.ticker='KLAC'  and ap.day<'2017-06-13'
                          ) open
               ON open.day=stats.FirstDay)
               LEFT JOIN (SELECT ap.ticker, ap.day, ap.close as Price
                          FROM AdjustedPrices ap 
                          WHERE ap.ticker='KLAC' and ap.day<'2017-06-13') close
               ON close.day=stats.LastDay)
            GROUP BY stats.year, stats.month) stock
            JOIN 
            (SELECT stats.year, stats.month, 100*AVG(cSect.Price/oSect.Price) as AvgRelChange
            FROM (((SELECT s.Sector, Year(ap.day) as year, Month(ap.day) as month, MIN(ap.day) as FirstDay,MAX(ap.day) as 
                     LastDay
               FROM (AdjustedPrices ap JOIN Securities s on s.ticker=ap.ticker)
               WHERE (s.Sector, s.Industry) = (SELECT s.Sector, s.Industry
                                  FROM Securities s
                                  WHERE s.ticker='KLAC') and ap.day<'2017-06-13'
               GROUP BY s.Sector,year, month) stats 
               LEFT JOIN (SELECT ap.ticker, s.sector, ap.day, ap.Open as Price
                          FROM AdjustedPrices ap JOIN Securities s on s.ticker=ap.ticker
                          WHERE (s.Sector, s.Industry) = (SELECT s.Sector, s.Industry
                                             FROM Securities s
                                             WHERE s.ticker='KLAC') and ap.day<'2017-06-13') oSect
               ON oSect.day=stats.FirstDay)
               LEFT JOIN (SELECT ap.ticker, s.sector, ap.day, ap.close as Price
                          FROM AdjustedPrices ap JOIN Securities s on s.ticker=ap.ticker
                          WHERE (s.Sector, s.Industry) = (SELECT s.Sector, s.Industry
                                                           FROM Securities s
                                                           WHERE s.ticker='KLAC') and ap.day<'2017-06-13') cSect
               ON cSect.day=stats.LastDay)
            GROUP BY stats.year, stats.month) industry on (stock.year=industry.year and stock.month=industry.month)
      GROUP BY stock.year, stock.month) counts,
(SELECT counts.year, MAX(counts.Difference) as max
   FROM (SELECT stock.year, stock.month, stock.AvgRelChange-industry.AvgRelChange as Difference
      FROM (SELECT stats.year, stats.month, 100*AVG(close.Price/open.Price) as AvgRelChange
            FROM (((SELECT ap.ticker, Year(ap.day) as year, Month(ap.day) as month, MIN(ap.day) as FirstDay,MAX(ap.day) as 
                     LastDay
               FROM AdjustedPrices ap
               WHERE ap.ticker='KLAC' and ap.day<'2017-06-13'
               GROUP BY year, month) stats 
               LEFT JOIN (SELECT ap.ticker, ap.day, ap.Open as Price
                          FROM AdjustedPrices ap
                          WHERE ap.ticker='KLAC'  and ap.day<'2017-06-13'
                          ) open
               ON open.day=stats.FirstDay)
               LEFT JOIN (SELECT ap.ticker, ap.day, ap.close as Price
                          FROM AdjustedPrices ap 
                          WHERE ap.ticker='KLAC' and ap.day<'2017-06-13') close
               ON close.day=stats.LastDay)
            GROUP BY stats.year, stats.month) stock
            JOIN 
            (SELECT stats.year, stats.month, 100*AVG(cSect.Price/oSect.Price) as AvgRelChange
            FROM (((SELECT s.Sector, Year(ap.day) as year, Month(ap.day) as month, MIN(ap.day) as FirstDay,MAX(ap.day) as 
                     LastDay
               FROM (AdjustedPrices ap JOIN Securities s on s.ticker=ap.ticker)
               WHERE (s.Sector, s.Industry) = (SELECT s.Sector, s.Industry
                                  FROM Securities s
                                  WHERE s.ticker='KLAC') and ap.day<'2017-06-13'
               GROUP BY s.Sector,year, month) stats 
               LEFT JOIN (SELECT ap.ticker, s.sector, ap.day, ap.Open as Price
                          FROM AdjustedPrices ap JOIN Securities s on s.ticker=ap.ticker
                          WHERE (s.Sector, s.Industry) = (SELECT s.Sector, s.Industry
                                             FROM Securities s
                                             WHERE s.ticker='KLAC') and ap.day<'2017-06-13') oSect
               ON oSect.day=stats.FirstDay)
               LEFT JOIN (SELECT ap.ticker, s.sector, ap.day, ap.close as Price
                          FROM AdjustedPrices ap JOIN Securities s on s.ticker=ap.ticker
                          WHERE (s.Sector, s.Industry) = (SELECT s.Sector, s.Industry
                                                           FROM Securities s
                                                           WHERE s.ticker='KLAC') and ap.day<'2017-06-13') cSect
               ON cSect.day=stats.LastDay)
            GROUP BY stats.year, stats.month) industry on (stock.year=industry.year and stock.month=industry.month)
      GROUP BY stock.year, stock.month) counts
GROUP BY counts.year) max
where max.max=counts.Difference
group by counts.year;
---------------------------------------------------------------------------------------
-- Top 5 Performers in 2016
SELECT rel1.year, rel1.ticker, rel1.Relative, count(*) as Place
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
WHERE rel1.year=rel2.year and rel1.Relative<=rel2.Relative and rel1.year=2016
GROUP BY rel1.year, rel1.ticker
HAVING Place <= 5
ORDER BY rel1.year, Place DESC;
   

-- Performance of the sector of stock in 2016
SELECT stats.*, AVG(oSect.Price) as AvgOpen, AVG(cSect.Price) as AvgClose,
      AVG(cSect.Price-oSect.Price) as AvgAbsChange,
      100*AVG(cSect.Price/oSect.Price) as AvgRelChange
FROM (((SELECT s.Sector, Year(ap.day) as year, Month(ap.day) as month, MIN(ap.day) as FirstDay,MAX(ap.day) as 
         LastDay, AVG(ap.Volume) as AvgVol, AVG(ap.low) as MonthlyLow, 
         AVG(ap.high) as MonthlyHigh
         FROM (AdjustedPrices ap JOIN Securities s on s.ticker=ap.ticker)
         WHERE (s.Sector, s.Industry) = (SELECT s.Sector, s.Industry
                            FROM Securities s
                            WHERE s.ticker='KLAC') and YEAR(ap.day)=2016
         GROUP BY s.Sector,year, month) stats 
LEFT JOIN (SELECT ap.ticker, s.sector, ap.day, ap.Open as Price
           FROM AdjustedPrices ap JOIN Securities s on s.ticker=ap.ticker
           WHERE (s.Sector, s.Industry) = (SELECT s.Sector, s.Industry
                              FROM Securities s
                              WHERE s.ticker='KLAC') and YEAR(ap.day)=2016) oSect
ON oSect.day=stats.FirstDay)
LEFT JOIN (SELECT ap.ticker, s.sector, ap.day, ap.close as Price
           FROM AdjustedPrices ap JOIN Securities s on s.ticker=ap.ticker
           WHERE (s.Sector, s.Industry) = (SELECT s.Sector, s.Industry
                                            FROM Securities s
                                            WHERE s.ticker='KLAC') and YEAR(ap.day)=2016) cSect
   ON cSect.day=stats.LastDay)
GROUP BY stats.year, stats.month;


-- performance of stock in 2016
SELECT stats.*, AVG(open.Price) as AvgOpen, AVG(close.Price) as AvgClose,
      AVG(close.Price-open.Price) as AvgAbsChange,
      100*AVG(close.Price/open.Price) as AvgRelChange
FROM (((SELECT ap.ticker, Year(ap.day) as year, Month(ap.day) as month, MIN(ap.day) as FirstDay,MAX(ap.day) as 
         LastDay, AVG(ap.Volume) as AvgVol, AVG(ap.low) as MonthlyLow, 
         AVG(ap.high) as MonthlyHigh
   FROM AdjustedPrices ap
   WHERE ap.ticker='KLAC' and YEAR(ap.day)=2016
   GROUP BY year, month) stats 
   LEFT JOIN (SELECT ap.ticker, ap.day, ap.Open as Price
              FROM AdjustedPrices ap
              WHERE ap.ticker='KLAC' and YEAR(ap.day)=2016
              ) open
   ON open.day=stats.FirstDay)
   LEFT JOIN (SELECT ap.ticker, ap.day, ap.close as Price
              FROM AdjustedPrices ap 
              WHERE ap.ticker='KLAC' and YEAR(ap.day)=2016) close
   ON close.day=stats.LastDay)
GROUP BY stats.year, stats.month;
<<<<<<< HEAD
=======




-- Individual 7

SELECT month, mainTicker, topFive, PriceDifference, VolumeDifference
FROM (
SELECT MONTHNAME(p1.day) as month, p1.ticker as mainTicker, p2.ticker as topFive, p1.close - p2.close as PriceDifference, v1.vTotal - v2.vtotal as VolumeDifference
FROM
   AdjustedPrices p1, AdjustedPrices p2,

   (SELECT max(day) as start
   FROM AdjustedPrices p
   WHERE YEAR(p.day) = 2016
   GROUP BY MONTH(p.day)) day,

   (SELECT MONTH(p.day) as mon, p.ticker, SUM(volume) as vTotal
   FROM AdjustedPrices p
   WHERE YEAR(p.day) = 2016
   GROUP BY MONTHNAME(p.day), p.ticker) v1,

   (SELECT MONTH(p.day) as mon, p.ticker, SUM(volume) as vTotal
   FROM AdjustedPrices p
   WHERE YEAR(p.day) = 2016
   GROUP BY MONTHNAME(p.day), p.ticker) v2,

   (SELECT rel1.ticker
   FROM (SELECT tDays.year, tDays.ticker, 100*(ap2.Close/ap1.Open) as Relative
   FROM (((SELECT YEAR(day) as year, ticker, min(day) as YearOpen, max(day)
            as YearClose FROM AdjustedPrices p
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
   WHERE rel1.year=rel2.year and rel1.Relative<=rel2.Relative and rel1.year=2016
   GROUP BY rel1.year, rel1.ticker
   HAVING count(*) <= 5) top

WHERE p1.ticker = 'KLAC' and YEAR(p1.day) = 2016 and p2.ticker = top.ticker and
   YEAR(p2.day) = 2016 and p1.day = p2.day and p1.day = day.start and MONTH(p1.day) = v1.mon and v1.mon = v2.mon and v1.ticker = p1.ticker and v2.ticker = p2.ticker
ORDER BY p1.day, p2.ticker) f;


-- Individual 8

SELECT p1.ticker as main, p2.ticker as other, p3.close / p1.open as mainRelativeGrowth, p4.close / p2.open as otherRelativeGrowth,
   v1.vTotal as mainVolumeTraded, v2.vTotal as otherVolumeTraded
FROM AdjustedPrices p1, AdjustedPrices p2, AdjustedPrices p3, AdjustedPrices p4,
   (SELECT p1.ticker as t1, p2.ticker as t2, IF (MAX(p1.day) < MAX(p2.day), MAX(p1.day), MAX(p2.day)) as max
   FROM AdjustedPrices p1, AdjustedPrices p2
   WHERE p1.ticker = 'KLAC' and p2.ticker = 'DISCK') endDate,

   (SELECT MIN(p1.day) as start
   FROM AdjustedPrices p1,
      (SELECT p1.ticker as t1, p2.ticker as t2, IF (MAX(p1.day) < MAX(p2.day), MAX(p1.day), MAX(p2.day)) as max
      FROM AdjustedPrices p1, AdjustedPrices p2
      WHERE p1.ticker = 'KLAC' and p2.ticker = 'DISCK') c
   WHERE p1.ticker = c.t1 and YEAR(p1.day) = YEAR(c.max)) startDate,

   (SELECT p.ticker, SUM(volume) vTotal
   FROM AdjustedPrices p
   WHERE YEAR(p.day) = 2016
   GROUP BY p.ticker) v1,

   (SELECT p.ticker, SUM(volume) vTotal
   FROM AdjustedPrices p
   WHERE YEAR(p.day) = 2016
   GROUP BY p.ticker) v2


WHERE p1.ticker = p3.ticker and p2.ticker = p4.ticker and p1.ticker = endDate.t1 and p2.ticker = endDate.t2 and p1.day = endDate.max and p2.day = endDate.max and p3.day = p4.day and p3.day = startDate.start and
   v1.ticker = p1.ticker and v2.ticker = p2.ticker;







>>>>>>> b7667dcf50c528c7a1c43c71c188c3bb6005ee78

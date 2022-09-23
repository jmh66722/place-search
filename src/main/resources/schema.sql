DROP TABLE IF EXISTS `KEYWORD_STATISTIC` CASCADE;

CREATE TABLE `KEYWORD_STATISTIC`
(
    `keyword`           varchar(100) NOT NULL, --키워드
    `search_count`      bigint      NOT NULL, --검색 횟수
    `modified_at`       datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    PRIMARY KEY (`keyword`)
);
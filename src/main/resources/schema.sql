DROP TABLE IF EXISTS `KEYWORD_STATISTICS` CASCADE;
DROP TABLE IF EXISTS `SEARCH_HISTORY` CASCADE;

CREATE TABLE `KEYWORD_STATISTICS`
(
    `keyword`           varchar(100) NOT NULL, --키워드
    `total_count`      bigint      NOT NULL, --검색 횟수
    `modified_at`       datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    PRIMARY KEY (`keyword`)
);

CREATE TABLE `SEARCH_HISTORY`
(
    `id`                bigint      NOT NULL AUTO_INCREMENT,
    `keyword`           varchar(100) NOT NULL, --키워드
    `result`            clob        NOT NULL, --결과
    `created_at`        datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    PRIMARY KEY (`id`)
);
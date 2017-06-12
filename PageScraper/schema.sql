drop schema if exists PAGE_SCRAPER;
create schema PAGE_SCRAPER;
use PAGE_SCRAPER;

create table search_query(
	id int primary key auto_increment,
    `query` longtext not null,
    `time` timestamp not null
);

create table query_results(
	id int primary key auto_increment,
    query_id int not null,
    link longtext not null,
    `type` varchar(50) not null, 
    foreign key(query_id) references search_query(id)
);


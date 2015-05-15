drop table FOLLOW;
drop table TWEET;
drop table TWI_USER;

create table TWI_USER (
    ID int primary key auto_increment,
    NAME varchar(20) unique not null,
    EMAIL varchar(100) unique not null,
    PASSWORD varchar(255) not null,
    INS_TIME timestamp not null,
    UPD_TIME timestamp
);

create table TWEET (
    ID int primary key auto_increment,
    USER_ID int not null,
    CONTENT varchar(140) not null,
    INS_TIME timestamp not null,
    UPD_TIME timestamp,
    foreign key(USER_ID) references TWI_USER(ID)
);

create table FOLLOW (
    ID int primary key auto_increment,
    USER_ID int not null,
    FOLLOW_USER_ID int not null,
    FLAG boolean not null,
    INS_TIME timestamp not null,
    UPD_TIME timestamp,
    foreign key(USER_ID) references TWI_USER(ID),
    foreign key(FOLLOW_USER_ID) references TWI_USER(ID)
);

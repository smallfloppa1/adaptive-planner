CREATE TYPE day_of_week AS ENUM (
    'MON',
    'TUE',
    'WED',
    'THU',
    'FRI',
    'SAT',
    'SUN'
    );

CREATE TYPE event_type AS ENUM (
    'WORK',
    'UNI_CLASS',
    'COMMUTE',
    'OTHER'
    );
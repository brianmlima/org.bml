
BEGIN ;
CREATE TABLE public.parse_error
(
    created_at timestamp DEFAULT NOW(),
    class_name varchar(256),
    host_name varchar 256,
    uri varchar(5000),
    reason varchar(5000)
);

COMMIT;
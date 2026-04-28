CREATE TABLE IF NOT EXISTS public.tenants (
                                              id          BIGSERIAL PRIMARY KEY,
                                              name        VARCHAR(100) NOT NULL,
    slug        VARCHAR(50)  NOT NULL UNIQUE,
    schema_name VARCHAR(50)  NOT NULL UNIQUE,
    active      BOOLEAN      NOT NULL DEFAULT true,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
    );
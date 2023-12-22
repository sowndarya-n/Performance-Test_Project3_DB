
SET profiling = 1;

select f.film_id, f.title, fa.film_id, fa.actor_id
from film f
join film_actor fa
on f.film_id = fa.film_id;


select f.film_id, r.rental_id
from film f
join inventory i
on f.film_id = i.film_id
join rental r
on r.inventory_id = i.inventory_id;


select f.film_id, f.title, fa.film_id, fa.actor_id, a.actor_id
from film f
join film_actor fa
on f.film_id = fa.film_id
join actor a
on a.actor_id = fa.actor_id;


select f.film_id, r.rental_id, c.customer_id
from film f
join inventory i
on f.film_id = i.film_id
join rental r
on r.inventory_id = i.inventory_id
join customer c
on c.customer_id = r.customer_id;


select ac.first_name, ac.last_name
from actor ac, film f, film_actor fa
where ac.actor_id = fa.actor_id
and f.film_id = fa.film_id
and f.title = 'CHICAGO NORTH';

select c.first_name, c.last_name, f.title
from customer c, rental r, inventory i, film f
where c.customer_id = r.customer_id
and r.inventory_id = i.inventory_id
and i.film_id = f.film_id
and f.title = 'CHICAGO NORTH';

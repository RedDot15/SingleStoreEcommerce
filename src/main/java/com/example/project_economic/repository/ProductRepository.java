package com.example.project_economic.repository;

import com.example.project_economic.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    @Query(value = "SELECT p FROM ProductEntity p WHERE p.isActive = true AND p.isDeleted = false AND p.categoryEntity.isActive = true")
    List<ProductEntity> findAllActive();

    @Query(value = "SELECT * FROM product WHERE category_id = :categoryId AND is_deleted = false", nativeQuery = true)
    List<ProductEntity> findAllByCategoryId(Long categoryId);

    @Query("SELECT EXISTS (" +
            "   SELECT 1 FROM ProductEntity p" +
            "   WHERE p.id = :id AND p.isDeleted = false" +
            ")")
    boolean existsById(Long id);

    @Query("SELECT EXISTS (" +
            "   SELECT 1 FROM ProductEntity p" +
            "   WHERE p.name = :name AND p.isDeleted = false" +
            ")")
    Boolean existsByName(String name);

    @Query("SELECT EXISTS (" +
            "   SELECT 1 FROM ProductEntity p" +
            "   WHERE p.name = :name AND p.id <> :id AND p.isDeleted = false" +
            ")")
    Boolean existsByNameExceptId(String name, Long id);

    ProductEntity findFirstById(Long id);

    @Query(value = "select p.category_id, count(p.product_id) productCount from products p where p.is_actived=true group by p.category_id order by productCount desc", nativeQuery = true)
    List<Object[]> countProductByCategoryId();

    @Query(value = "select * from products as p inner join categories as c on p.category_id=c.category_id where c.category_id=?1 and c.is_actived=true and p.is_actived=true", nativeQuery = true)
    List<ProductEntity> findAllProductByCategoryId(Long category_id);

    //    @Query(value = "select p.* from products as p inner join categories as c on p.category_id=c.category_id where c.is_actived=true and p.is_actived=true",nativeQuery = true)
//    Page<ProductEntity> findAllProductIsAvtived(Pageable pageable);
    @Query(value = "SELECT p.id FROM product AS p JOIN category c ON p.category_id = c.id WHERE p.is_active = true AND c.is_active = true", nativeQuery = true)
    List<Long> findAllActiveId();

    @Query(value = "SELECT * FROM product WHERE id IN :idList ", nativeQuery = true)
    Page<ProductEntity> findAllActiveProductByPage(Pageable pageable, @Param("idList") List<Long> idList);

//    @Query(value = "select * from products as p where p.is_actived=true",nativeQuery = true)
//    List<ProductEntity>findAllProductIsAvtived(Pageable pageable);

    @Query(value = "select * from products where is_actived = true and name like ?1 limit ?2 offset ?3", nativeQuery = true)
    List<ProductEntity> findAllProductByKeyword(String keyword, Integer pageSize, Integer offsetNumber);

    @Query(value = "select count(product_id) from products where name like ?1 and is_actived = true", nativeQuery = true)
    Integer countProductByKeyword(String keyword);

    @Query(value = "select * from products where is_actived=true and category_id=?1 limit ?2 offset ?3  ", nativeQuery = true)
    List<ProductEntity> findAllProductByCategory(Long id, int pageSize, int offsetNumber);

    @Query(value = "select  count(product_id) from products where category_id=?1 and is_actived = true", nativeQuery = true)
    Integer countProductByCategory(Long id);

    @Query(value = "select * from products where is_actived = true and sale_price between ?1 and ?2 limit ?3 offset ?4", nativeQuery = true)
    List<ProductEntity> findAllProductByPriceAndPagination(int first_price, int second_price, int pageSize, int offsetNumber);

    @Query(value = "select count(product_id) from products where sale_price between ?1 and ?2 and is_actived = true", nativeQuery = true)
    Integer countProductByPrice(int first_price, int second_price);

    @Query(value = "select * from products where product_id in ?1", nativeQuery = true)
    List<ProductEntity> findByIds(List<Long> ids);


}

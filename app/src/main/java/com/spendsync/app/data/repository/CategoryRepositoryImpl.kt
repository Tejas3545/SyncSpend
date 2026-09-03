package com.spendsync.app.data.repository

import com.spendsync.app.data.local.db.dao.CategoryDao
import com.spendsync.app.data.local.db.entities.CategoryEntity
import com.spendsync.app.domain.model.Category
import com.spendsync.app.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao
) : CategoryRepository {

    override fun getAllCategories(): Flow<List<Category>> {
        return categoryDao.getAllCategories().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun addCategory(category: Category) {
        val entity = category.toEntity()
        categoryDao.insert(entity)
    }

    override suspend fun deleteCategory(id: String) {
        categoryDao.deleteById(id)
    }

    private fun CategoryEntity.toDomainModel(): Category {
        return Category(
            id = id,
            name = name,
            emoji = emoji
        )
    }

    private fun Category.toEntity(): CategoryEntity {
        return CategoryEntity(
            id = id,
            name = name,
            emoji = emoji,
            isDefault = false
        )
    }
}

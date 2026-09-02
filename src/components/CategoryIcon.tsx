import React from 'react';
import {
  Music2,
  Utensils,
  Car,
  ShoppingBag,
  ShoppingCart,
  HeartPulse,
  Zap,
  Home,
  Plane,
  User,
  Coffee,
  Tag,
  CreditCard,
  Tv,
  Briefcase,
  Smartphone,
} from 'lucide-react';

interface CategoryIconProps {
  iconName?: string;
  className?: string;
}

export const CategoryIcon: React.FC<CategoryIconProps> = ({
  iconName = '',
  className = 'w-4 h-4',
}) => {
  const normalized = iconName.toLowerCase();

  if (normalized.includes('music') || normalized.includes('spotify') || normalized.includes('entertainment')) {
    return <Music2 className={className} />;
  }
  if (
    normalized.includes('utensil') ||
    normalized.includes('dining') ||
    normalized.includes('food') ||
    normalized.includes('grocer') ||
    normalized.includes('eat')
  ) {
    return <Utensils className={className} />;
  }
  if (
    normalized.includes('car') ||
    normalized.includes('uber') ||
    normalized.includes('transit') ||
    normalized.includes('transport') ||
    normalized.includes('taxi')
  ) {
    return <Car className={className} />;
  }
  if (normalized.includes('shopping-bag') || normalized.includes('shop')) {
    return <ShoppingBag className={className} />;
  }
  if (normalized.includes('cart')) {
    return <ShoppingCart className={className} />;
  }
  if (normalized.includes('heart') || normalized.includes('health') || normalized.includes('medical')) {
    return <HeartPulse className={className} />;
  }
  if (normalized.includes('zap') || normalized.includes('util') || normalized.includes('bill') || normalized.includes('power')) {
    return <Zap className={className} />;
  }
  if (normalized.includes('home') || normalized.includes('house') || normalized.includes('rent')) {
    return <Home className={className} />;
  }
  if (normalized.includes('plane') || normalized.includes('travel') || normalized.includes('flight')) {
    return <Plane className={className} />;
  }
  if (normalized.includes('coffee') || normalized.includes('cafe')) {
    return <Coffee className={className} />;
  }
  if (normalized.includes('user') || normalized.includes('personal')) {
    return <User className={className} />;
  }
  if (normalized.includes('tv') || normalized.includes('stream')) {
    return <Tv className={className} />;
  }
  if (normalized.includes('work') || normalized.includes('briefcase')) {
    return <Briefcase className={className} />;
  }
  if (normalized.includes('phone') || normalized.includes('mobile')) {
    return <Smartphone className={className} />;
  }
  if (normalized.includes('card') || normalized.includes('pay')) {
    return <CreditCard className={className} />;
  }

  return <Tag className={className} />;
};

export const AVAILABLE_CATEGORY_ICONS = [
  { id: 'utensils', label: 'Dining & Food', icon: Utensils },
  { id: 'music', label: 'Music & Media', icon: Music2 },
  { id: 'car', label: 'Transport & Uber', icon: Car },
  { id: 'shopping-bag', label: 'Shopping', icon: ShoppingBag },
  { id: 'shopping-cart', label: 'Groceries', icon: ShoppingCart },
  { id: 'heart-pulse', label: 'Health', icon: HeartPulse },
  { id: 'zap', label: 'Utilities & Bills', icon: Zap },
  { id: 'home', label: 'Housing', icon: Home },
  { id: 'plane', label: 'Travel', icon: Plane },
  { id: 'coffee', label: 'Coffee & Drinks', icon: Coffee },
  { id: 'user', label: 'Personal', icon: User },
  { id: 'tv', label: 'Streaming', icon: Tv },
  { id: 'briefcase', label: 'Business', icon: Briefcase },
  { id: 'tag', label: 'General / Other', icon: Tag },
];

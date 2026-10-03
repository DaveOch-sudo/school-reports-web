import { useState } from "react";
import type { ReactNode } from "react";

export interface TabItem {
  id: string;
  label: string;
  content: ReactNode;
  disabled?: boolean;
}

interface TabsProps {
  tabs: TabItem[];
  defaultTab?: string;
  className?: string;
}

function Tabs({ tabs, defaultTab, className = "" }: TabsProps) {
  const [activeTab, setActiveTab] = useState(() => {
    if (tabs.some((tab) => tab.id === defaultTab && !tab.disabled)) {
      return defaultTab!;
    }

    return tabs.find((tab) => !tab.disabled)?.id ?? "";
  });

  const selectedTab = tabs.find((tab) => tab.id === activeTab);

  if (tabs.length === 0) {
    return null;
  }

  return (
    <div className={`w-full ${className}`}>
      <div
        role="tablist"
        aria-label="Content tabs"
        className="fles gap-1 overflow-x-auto border-b border-gray-200"
      >
        {tabs.map((tab) => {
          const isActive = tab.id === activeTab;

          return (
            <button
              key={tab.id}
              id={`tab-${tab.id}`}
              type="button"
              role="tab"
              aria-selected={isActive}
              aria-controls={`panel-${tab.id}`}
              disabled={tab.disabled}
              onClick={() => setActiveTab(tab.id)}
              className={`
                                shrink-0 border-b-2 px-4 py-3 text-sm font-medium
                                transition-colors
                                focus-visible:outline-2 focus-visible:outline-offset-[-2px]
                                focus-visible:outline-blue-600
                                disabled:cursor-not-allowed disabled:opacity-50
                                ${
                                  isActive
                                    ? "border-blue-600 text-blue-700"
                                    : "border-transparent text-gray-500 hover:border-gray-300 hover:text-gray-700"
                                }
                            `}
            >
              {tab.label}
            </button>
          );
        })}
      </div>

      {selectedTab && (
        <div
          id={`panel-${selectedTab.id}`}
          role="tabpanel"
          aria-labelledby={`tab-${selectedTab.id}`}
          tabIndex={0}
          className="py-4 focus-visible:outline-2 focus-visible:outline-blue-600"
        >
          {selectedTab.content}
        </div>
      )}
    </div>
  );
}

export default Tabs;
